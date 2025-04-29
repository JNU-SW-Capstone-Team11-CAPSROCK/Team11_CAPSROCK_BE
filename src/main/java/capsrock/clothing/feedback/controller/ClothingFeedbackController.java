package capsrock.clothing.feedback.controller;

import capsrock.clothing.feedback.service.ClothingFeedbackService;
import capsrock.dto.request.ClothingFeedbackRequest;
import capsrock.member.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClothingFeedbackController {

    private final ClothingFeedbackService clothingFeedbackService;

    @PostMapping
    public ResponseEntity<Void> receiveFeedback(/*@LoginMember */MemberInfoDTO memberInfoDTO,
            ClothingFeedbackRequest clothingFeedbackRequest) {
//        clothingFeedbackService.processFeedback(memberInfoDTO, clothingFeedbackRequest);
        return ResponseEntity.ok().build();
    }

}
