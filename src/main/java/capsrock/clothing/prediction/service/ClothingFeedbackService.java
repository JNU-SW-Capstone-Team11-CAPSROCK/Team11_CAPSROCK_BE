package capsrock.clothing.prediction.service;

import capsrock.clothing.prediction.dto.request.ClothingFeedbackRequest;
import capsrock.clothing.prediction.model.entity.ClothingPrediction;
import capsrock.clothing.prediction.model.vo.Score;
import capsrock.clothing.prediction.model.vo.Status;
import capsrock.clothing.prediction.repository.ClothingPredictionRepository;
import capsrock.member.dto.service.MemberInfoDTO;
import capsrock.member.exception.MemberException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClothingFeedbackService {
    private final ClothingPredictionRepository clothingPredictionRepository;

    @Transactional
    public void saveFeedback(MemberInfoDTO memberInfoDTO,
            ClothingFeedbackRequest clothingFeedbackRequest) {

//        if (havePending(memberInfoDTO.id()))
//            throw new FeedbackPendingException("이미 대기중인 피드백이 있습니다");

        ClothingPrediction prediction = clothingPredictionRepository.findByMemberId(
                memberInfoDTO.id()).orElseThrow(
                () -> new MemberException("id가 %d인 회원을 찾지 못했습니다.".formatted(memberInfoDTO.id())));

        applyFeedbackToEntity(prediction, clothingFeedbackRequest);
    }

    private void applyFeedbackToEntity(ClothingPrediction prediction,
            ClothingFeedbackRequest clothingFeedbackRequest) {
        Score score = new Score(
                clothingFeedbackRequest.morningScore(),
                clothingFeedbackRequest.noonScore(),
                clothingFeedbackRequest.eveningScore()
        );
        prediction.receiveFeedback(score, clothingFeedbackRequest.comment(), LocalDateTime.now());
    }

    public Boolean havePending(Long memberId) {
        return clothingPredictionRepository.existsByMemberIdAndStatus(memberId, Status.PENDING);
    }
}
