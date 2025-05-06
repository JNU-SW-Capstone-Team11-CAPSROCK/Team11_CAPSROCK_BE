package capsrock.clothing.service;

import capsrock.clothing.dto.service.NewPredictionDataDTO;
import capsrock.clothing.dto.service.PredictionInfoDTO;
import capsrock.clothing.model.entity.ClothingPrediction;
import capsrock.clothing.model.vo.Status;
import capsrock.clothing.repository.ClothingPredictionRepository;
import capsrock.member.exception.MemberNotFoundException;
import capsrock.member.model.entity.Member;
import capsrock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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