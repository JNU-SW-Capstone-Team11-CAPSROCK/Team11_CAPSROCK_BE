package capsrock.clothing.service;

import capsrock.clothing.dto.client.request.ClothingData;
import capsrock.clothing.dto.service.AggregatedUserDataDTO;
import capsrock.clothing.dto.service.PredictionInfoDTO;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Status;
import capsrock.member.dto.service.RecentLocationDTO;
import capsrock.member.service.MemberService;
import capsrock.weather.client.WeatherInfoClient;
import capsrock.weather.dto.response.DailyWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDataAggregationService {

    private static final Integer MAX_PREDICTION_DATA = 20;

    private final MemberService memberService;
    private final WeatherInfoClient weatherInfoClient;

    public Map<Long, AggregatedUserDataDTO> aggregateUserData(
            List<PredictionInfoDTO> predictionInfos) {
        // 1. 사용자별 기록 그룹화 (ARCHIVED 상태만, 최대 MAX_PREDICTION_DATA개)
        Map<Long, List<ClothingData>> userRecordsMap = new HashMap<>();
        Map<Long, RecentLocationDTO> locationMap = new HashMap<>(); // 위치 정보 캐싱용

        for (PredictionInfoDTO info : predictionInfos) {
            Long memberId = info.memberId();

            if (!userRecordsMap.containsKey(memberId)) {
                userRecordsMap.put(memberId, new ArrayList<>());
                // 위치 정보는 사용자별로 한 번만 조회
                locationMap.put(memberId, memberService.getRecentLocationById(memberId));
            }

            // ARCHIVED 상태이고, 최대 개수를 넘지 않았을 때만 기록 추가
            if (info.status().equals(Status.ARCHIVED)
                    && userRecordsMap.get(memberId).size() < MAX_PREDICTION_DATA) {
                userRecordsMap.get(memberId).add(
                        new ClothingData(
                                info.predictedAt(),
                                info.feelsLikeTemp(),
                                info.correction(),
                                info.score(),
                                info.comment()
                        )
                );
            }
        }

        // 2. 사용자별 오늘 체감 온도 계산
        Map<Long, FeelsLikeTemp> feelsLikeMap = new HashMap<>();
        locationMap.forEach((memberId, location) -> {
            DailyWeatherResponse dailyWeatherResponse = weatherInfoClient.getDailyWeatherResponse(
                    location.latitude(), location.longitude(), 1);
            DailyWeatherResponse.FeelsLike todayFeelsLike = dailyWeatherResponse.list().getFirst()
                    .feelsLike();
            feelsLikeMap.put(memberId,
                    new FeelsLikeTemp(todayFeelsLike.morn(), todayFeelsLike.day(),
                            todayFeelsLike.eve()));
        });

        // 3. 최종 AggregatedUserDataDTO 맵 생성
        Map<Long, AggregatedUserDataDTO> aggregatedDataMap = new HashMap<>();
        userRecordsMap.forEach((memberId, records) -> {
            aggregatedDataMap.put(memberId, new AggregatedUserDataDTO(
                    memberId,
                    records, // 이미 ClothingData 리스트임
                    locationMap.get(memberId),
                    feelsLikeMap.get(memberId)
            ));
        });

        return aggregatedDataMap;
    }
}