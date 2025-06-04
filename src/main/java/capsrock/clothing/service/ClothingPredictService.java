package capsrock.clothing.service;

import capsrock.clothing.dto.client.response.UserPrediction;
import capsrock.clothing.dto.service.AggregatedUserDataDTO;
import capsrock.clothing.dto.service.NewPredictionDataDTO;
import capsrock.clothing.dto.service.PredictionInfoDTO;
import capsrock.clothing.model.vo.Location;
import capsrock.clothing.model.vo.Status;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClothingPredictService {

    private final ClothingPredictionDataService dataService;
    private final UserDataAggregationService aggregationService;
    private final PredictionRequestService requestService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void predict() {
        log.info("Starting scheduled clothing prediction process.");
        try {
            // 1. 처리할 예측 데이터 조회
            List<PredictionInfoDTO> allPredictions = dataService.findAllPredictionsSortedByDate();

            // 2. COMPLETED 상태인 예측 ID 목록 추출 및 ARCHIVED로 상태 변경 요청
            List<Long> completedPredictionIds = allPredictions.stream()
                    .filter(p -> p.status().equals(Status.COMPLETED))
                    .map(PredictionInfoDTO::predictionId)
                    .toList();
            if (!completedPredictionIds.isEmpty()) {
                log.info("Archiving {} completed predictions.", completedPredictionIds.size());
                dataService.archiveCompletedPredictions(completedPredictionIds);
            }

            List<Long> pendingMemberIds = dataService.getMemberIdByPending();

            // 3. 사용자별 데이터 집계 (위치, 날씨 포함) - ARCHIVED 상태 데이터 기반
            List<PredictionInfoDTO> processablePredictions = allPredictions.stream()
                    //.filter(p -> p.status().equals(Status.ARCHIVED) || completedPredictionIds.contains(p.predictionId())) // 방금 업데이트된 것도 포함하도록 필터링
                    .filter(p -> !pendingMemberIds.contains(p.memberId())) // PENDING 상태는 제외 (혹은 비즈니스 로직에 따라 조정)
                    .toList();

            Map<Long, AggregatedUserDataDTO> aggregatedDataMap = aggregationService.aggregateUserData(
                    processablePredictions);

            // 4. Gemini 요청 데이터 준비 (충분한 데이터가 있는 사용자) 및 기본 예측 생성 (데이터 부족 사용자)
            PredictionRequestService.PreparedRequestData preparedRequest = requestService.prepareRequestData(
                    aggregatedDataMap);
            List<UserPrediction> defaultPredictions = preparedRequest.defaultPredictions();

            // 5. Gemini API 호출
            log.info("Requesting predictions from Gemini for {} users.",
                    preparedRequest.dataForGemini().size());
            List<UserPrediction> geminiPredictions = requestService.requestPredictions(
                    preparedRequest.dataForGemini());

            // 6. 모든 예측 결과 통합 (Gemini 결과 + 기본 결과)
            List<UserPrediction> finalUserPredictions = new ArrayList<>(defaultPredictions);
            finalUserPredictions.addAll(geminiPredictions);

            if (finalUserPredictions.isEmpty()) {
                log.info("No predictions to save.");
                return;
            }

            // 7. 새로운 예측 데이터 DTO 생성 (저장용)
            LocalDate today = LocalDate.now();
            List<NewPredictionDataDTO> newPredictionsToSave = finalUserPredictions.stream()
                    .map(userPrediction -> {
                        AggregatedUserDataDTO userData = aggregatedDataMap.get(
                                userPrediction.userId());
                        if (userData == null) {
                            log.warn("Aggregated data not found for user ID: {}",
                                    userPrediction.userId());
                            return null; // 또는 기본 위치/온도로 처리
                        }
                        Location location = new Location(userData.location().longitude(),
                                userData.location().latitude());
                        return new NewPredictionDataDTO(
                                userPrediction.userId(),
                                userPrediction.predictedCorrectionValues(),
                                location,
                                today,
                                userData.todaysFeelsLikeTemp()
                        );
                    })
                    .filter(Objects::nonNull) // userData가 없는 경우 제외
                    .collect(Collectors.toList());

            // 8. 새로운 예측 결과 저장
            log.info("Saving {} new predictions.", newPredictionsToSave.size());
            dataService.saveNewPredictions(newPredictionsToSave);

            log.info("Scheduled clothing prediction process finished successfully.");

        } catch (Exception e) {
            log.error("Error during scheduled clothing prediction process", e);
        }
    }
}
