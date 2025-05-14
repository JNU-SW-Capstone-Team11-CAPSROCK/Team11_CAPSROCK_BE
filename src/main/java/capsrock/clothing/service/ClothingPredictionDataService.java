package capsrock.clothing.service;

import capsrock.clothing.dto.service.NewPredictionDataDTO;
import capsrock.clothing.dto.service.PredictionInfoDTO;
import capsrock.clothing.exception.PendingFeedbackNotFoundException;
import capsrock.clothing.model.entity.ClothingPrediction;
import capsrock.clothing.model.vo.Correction;
import capsrock.clothing.model.vo.FeelsLikeTemp;
import capsrock.clothing.model.vo.Location;
import capsrock.clothing.model.vo.Status;
import capsrock.clothing.repository.ClothingPredictionRepository;
import capsrock.member.exception.MemberNotFoundException;
import capsrock.member.model.entity.Member;
import capsrock.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClothingPredictionDataService {

    private final ClothingPredictionRepository clothingPredictionRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<PredictionInfoDTO> findAllPredictionsSortedByDate() {
        return clothingPredictionRepository.findAll(Sort.by(Sort.Direction.DESC, "predictedAt"))
                .stream()
                .map(this::mapToPredictionInfoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Correction findCorrectionsByMemberIdAndPending(Long memberId) {
        ClothingPrediction prediction = clothingPredictionRepository.findByMemberIdAndStatus(
                memberId, Status.PENDING);

        if (prediction == null) {
            throw new PendingFeedbackNotFoundException("PENDING인 예측 데이터를 찾을 수 없습니다.");
        }

        PredictionInfoDTO predictionInfoDTO = mapToPredictionInfoDTO(prediction);

        return predictionInfoDTO.correction();
    }

    @Transactional(readOnly = true)
    public Boolean hasPendingPrediction(Long memberId) {
        return clothingPredictionRepository.existsByMemberIdAndStatus(memberId, Status.PENDING);
    }

    @Transactional
    public void archiveCompletedPredictions(List<Long> predictionIdsToArchive) {
        if (predictionIdsToArchive.isEmpty()) {
            return;
        }
        List<ClothingPrediction> predictions = clothingPredictionRepository.findAllById(
                predictionIdsToArchive);
        predictions.forEach(prediction -> {
            if (prediction.getStatus().equals(Status.COMPLETED)) {
                prediction.changeToArchive();
            }
        });
    }

    @Transactional
    public void saveNewPredictions(List<NewPredictionDataDTO> newPredictions) {
        if (newPredictions.isEmpty()) {
            return;
        }
        List<ClothingPrediction> entitiesToSave = newPredictions.stream()
                .map(this::mapToClothingPrediction)
                .collect(Collectors.toList());
        clothingPredictionRepository.saveAll(entitiesToSave);
    }

    @Transactional
    public void saveNewPredictionForRegister(Long memberId, Double latitude, Double longitude, FeelsLikeTemp feelsLikeTemp) {
        LocalDateTime now = LocalDateTime.now();

        if (now.getHour() == 0){
            now = now.minusDays(1);
        }

        NewPredictionDataDTO newPrediction = new NewPredictionDataDTO(
                memberId, new Correction(0.0, 0.0, 0.0)
                , new Location(longitude, latitude), now.toLocalDate(), feelsLikeTemp
        );
        clothingPredictionRepository.save(mapToClothingPrediction(newPrediction));
    }

    private PredictionInfoDTO mapToPredictionInfoDTO(ClothingPrediction entity) {
        return PredictionInfoDTO.builder()
                .predictionId(entity.getId())
                .memberId(entity.getMember().getId())
                .predictedAt(entity.getPredictedAt())
                .score(entity.getScore())
                .correction(entity.getCorrection())
                .feelsLikeTemp(entity.getFeelsLikeTemp())
                .status(entity.getStatus())
                .comment(entity.getComment())
                .build();
    }

    private ClothingPrediction mapToClothingPrediction(NewPredictionDataDTO dto) {
        Member member = memberRepository.findById(dto.memberId()).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(dto.memberId()))
        );
        return new ClothingPrediction(
                member,
                dto.predictedCorrection(),
                dto.location(),
                dto.date(),
                dto.feelsLikeTemp()
        );
    }
}