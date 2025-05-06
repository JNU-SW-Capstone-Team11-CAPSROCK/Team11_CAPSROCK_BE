package capsrock.clothing.service;

import capsrock.clothing.client.ClothingGeminiClient;
import capsrock.clothing.dto.client.request.ClothingPredictionRequest;
import capsrock.clothing.dto.client.request.OneUserData;
import capsrock.clothing.dto.client.response.ClothingPredictionResponse;
import capsrock.clothing.dto.client.response.UserPrediction;
import capsrock.clothing.dto.service.AggregatedUserDataDTO;
import capsrock.clothing.model.vo.Correction;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionRequestService {

    private static final Integer MIN_PREDICTION_DATA = 5;
    private static final Integer CHUNK_SIZE = 10;

    private final ClothingGeminiClient clothingGeminiClient;

    // 내부 사용 DTO
    public record PreparedRequestData(List<OneUserData> dataForGemini,
                                      List<UserPrediction> defaultPredictions) {

    }

    public PreparedRequestData prepareRequestData(
            Map<Long, AggregatedUserDataDTO> aggregatedDataMap) {
        List<OneUserData> usersDataForGemini = new ArrayList<>();
        List<UserPrediction> defaultPredictions = new ArrayList<>();

        aggregatedDataMap.forEach((memberId, data) -> {
            if (data.pastRecords().size() >= MIN_PREDICTION_DATA) {
                usersDataForGemini.add(new OneUserData(
                        memberId,
                        data.pastRecords(),
                        data.todaysFeelsLikeTemp()
                ));
            } else {
                // 충분한 데이터가 없을 때 보정치 0으로 기본 예측 생성
                defaultPredictions.add(new UserPrediction(memberId, new Correction(0.0, 0.0, 0.0)));
            }
        });

        return new PreparedRequestData(usersDataForGemini, defaultPredictions);
    }

    public List<UserPrediction> requestPredictions(List<OneUserData> usersData) {
        if (usersData.isEmpty()) {
            return new ArrayList<>();
        }

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
                log.error("Error creating prediction request for chunk", e);
                // 필요시 실패한 청크에 대한 처리 추가
            }
        }

        if (futures.isEmpty()) {
            return new ArrayList<>();
        }

        // 모든 비동기 요청 완료 대기 및 결과 처리
        List<UserPrediction> geminiResults = new ArrayList<>();
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .flatMap(response -> response.userDataList().stream())
                            .collect(Collectors.toList()))
                    .thenAccept(geminiResults::addAll)
                    .exceptionally(ex -> {
                        log.error("Error during Gemini prediction requests", ex);
                        return null;
                    })
                    .get(); // 비동기 작업 완료 대기
        } catch (Exception e) {
            log.error("Error waiting for Gemini prediction results", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            // 예외 처리 전략에 따라 추가 작업 수행
        }

        return geminiResults;
    }
}