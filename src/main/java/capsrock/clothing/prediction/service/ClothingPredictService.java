package capsrock.clothing.prediction.service;

import capsrock.clothing.prediction.client.ClothingGeminiClient;
import capsrock.clothing.prediction.dto.client.request.ClothingData;
import capsrock.clothing.prediction.dto.client.request.ClothingPredictionRequest;
import capsrock.clothing.prediction.dto.client.request.OneUserData;
import capsrock.clothing.prediction.dto.client.response.ClothingPredictionResponse;
import capsrock.clothing.prediction.dto.client.response.UserPrediction;
import capsrock.clothing.prediction.dto.service.ClothingRecordDTO;
import capsrock.clothing.prediction.model.entity.ClothingPrediction;
import capsrock.clothing.prediction.model.vo.Correction;
import capsrock.clothing.prediction.model.vo.FeelsLikeTemp;
import capsrock.clothing.prediction.model.vo.Location;
import capsrock.clothing.prediction.model.vo.Status;
import capsrock.clothing.prediction.repository.ClothingPredictionRepository;
import capsrock.member.dto.service.RecentLocationDTO;
import capsrock.member.exception.MemberNotFoundException;
import capsrock.member.model.entity.Member;
import capsrock.member.repository.MemberRepository;
import capsrock.member.service.MemberService;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.DailyWeatherResponse;
import capsrock.weather.dto.response.DailyWeatherResponse.FeelsLike;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClothingPredictService {

    private static final Integer MAX_PREDICTION_DATA = 20;
    private static final Integer MIN_PREDICTION_DATA = 5;
    private static final Integer CHUNK_SIZE = 10;


    private final ClothingGeminiClient clothingGeminiClient;
    private final ClothingPredictionRepository clothingPredictionRepository;
    private final MemberService memberService;
    private final WeatherInfoClient weatherInfoClient;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void predict() throws JsonProcessingException, ExecutionException, InterruptedException {

        /*
         * 1. Status가 COMPLETE인걸 ARCHIVED로 바꾼다.
         * 2. 각 회원들에 대하여 Status가 ARCHIVED 인게 5개 이상이라면 그 회원의 데이터를 최근 순으로 최대 20개 가져온다
         * 2-1. 만약 5개 미만이라면 보정치를 0으로 두고 gemini에게 요청을 보내지 않는다.
         * 3. gemini에게 예측을 시킨다.
         * 4. 예측한걸 ClothingPrediction에 저장한다.
         * */

        //gemini에게 다음날 보정치 예측 시키기
//        clothingGeminiClient.getPrediction();

        List<ClothingPrediction> predictionList = clothingPredictionRepository.findAll(
                Sort.by(Sort.Direction.DESC, "predictedAt") //최근으로 내림차순
        );

        Map<Long, List<ClothingRecordDTO>> clothingRecordMap = new HashMap<>();
        Map<Long, RecentLocationDTO> recentLocationMap = new HashMap<>();
        Map<Long, FeelsLikeTemp> feelsLikeMap = new HashMap<>();
        List<UserPrediction> userPredictions = new ArrayList<>();

        for (ClothingPrediction prediction : predictionList) {
            if (prediction.getStatus().equals(Status.COMPLETED)) {
                prediction.changeToArchive();
            }

            if (!clothingRecordMap.containsKey(prediction.getId())) {
                // Sort.by(Sort.Direction.DESC, "predictedAt") 덕분에 가장 최근 예측 데이터로 기대함.

                clothingRecordMap.put(prediction.getId(), new ArrayList<>());
                recentLocationMap.put(prediction.getId(), memberService.getRecentLocationById(
                        prediction.getId()));
            }

            if (clothingRecordMap.get(prediction.getId()).size() >= MAX_PREDICTION_DATA) {
                continue;
            }

            if (prediction.getStatus().equals(Status.ARCHIVED)) {
                List<ClothingRecordDTO> recordList = clothingRecordMap.get(prediction.getId());

                recordList.add(
                        ClothingRecordDTO.builder()
                                .predictionDate(prediction.getPredictedAt())
                                .score(prediction.getScore())
                                .correction(prediction.getCorrection())
                                .feelsLikeTemp(prediction.getFeelsLikeTemp())
                                .build()
                );
            }


        }
        List<OneUserData> usersData = new ArrayList<>();

        clothingRecordMap.forEach((memberId, recordList) -> {

            RecentLocationDTO recentLocationDTO = recentLocationMap.get(memberId);
            DailyWeatherResponse dailyWeatherResponse = weatherInfoClient.getDailyWeatherResponse(
                    recentLocationDTO.latitude(), recentLocationDTO.longitude(), 1);

            FeelsLike todayFeelsLike = dailyWeatherResponse.list().getFirst().feelsLike();

            feelsLikeMap.put(memberId,
                    new FeelsLikeTemp(todayFeelsLike.morn(), todayFeelsLike.day(),
                            todayFeelsLike.eve()));

            if (recordList.size() >= MIN_PREDICTION_DATA) {

                List<ClothingData> clothingDataList = recordList.stream()
                        .map(record -> new ClothingData(
                                record.predictionDate(),
                                record.feelsLikeTemp(),
                                record.correction(),
                                record.score(),
                                record.comment()
                        ))
                        .toList();

                usersData.add(new OneUserData(memberId, clothingDataList, feelsLikeMap.get(memberId)));
            }

            if (recordList.size() < MIN_PREDICTION_DATA) { //충분한 데이터가 없을 때 보정치 0으로 하기
                userPredictions.add(
                        new UserPrediction(memberId, new Correction(0.0, 0.0, 0.0)));
            }
        });

        // 요청을 청크 단위로 나누기
        List<List<OneUserData>> chunkedUsersData = new ArrayList<>();
        for (int i = 0; i < usersData.size(); i += CHUNK_SIZE) {
            chunkedUsersData.add(
                    usersData.subList(i, Math.min(i + CHUNK_SIZE, usersData.size()))
            );
        }

        List<CompletableFuture<ClothingPredictionResponse>> futures = new ArrayList<>();

        // 각 청크에 대한 비동기 요청 생성
        for (List<OneUserData> chunk : chunkedUsersData) {
            try {
                ClothingPredictionRequest chunkRequest = new ClothingPredictionRequest(chunk);
                CompletableFuture<ClothingPredictionResponse> future =
                        clothingGeminiClient.getPrediction(chunkRequest);
                futures.add(future);
            } catch (JsonProcessingException e) {
            }
        }

        if (futures.isEmpty()) {
            return;
        }

        // 모든 비동기 요청 완료 대기 및 결과 처리
        // 모든 예측 결과를 DB에 저장
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)  // 모든 future의 결과 가져오기
                        .flatMap(response -> response.userDataList().stream())
                        .collect(Collectors.toList()))
                .thenAccept(userPredictions::addAll)
                .exceptionally(ex -> {
                    return null;
                })
                .get(); // 비동기 작업 완료 대기 (scheduled 메서드이므로 블로킹해도 괜찮음)

        List<ClothingPrediction> allUserPredictionEntities = new ArrayList<>();

        for (UserPrediction userPrediction : userPredictions) {
            Member foundMember = memberRepository.findById(userPrediction.userId()).orElseThrow(
                    () -> new MemberNotFoundException(
                            "id가 %d은 회원을 찾지 못했습니다.".formatted(userPrediction.userId())));

            Correction correction = userPrediction.predictedCorrectionValues();
            RecentLocationDTO recentLocationDTO = recentLocationMap.get(foundMember.getId());
            Location location = new Location(recentLocationDTO.longitude(),
                    recentLocationDTO.latitude());

            allUserPredictionEntities.add(
                    new ClothingPrediction(foundMember, correction, location, LocalDate.now(),
                            feelsLikeMap.get(foundMember.getId())
                    )
            );

        }

        clothingPredictionRepository.saveAll(allUserPredictionEntities);
    }

}
