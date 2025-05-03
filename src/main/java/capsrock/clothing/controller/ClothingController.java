package capsrock.clothing.controller;

import capsrock.clothing.dto.request.ClothingFeedbackRequest;
import capsrock.clothing.service.ClothingFeedbackService;
import capsrock.common.security.dto.CapsrockUserDetails;
import capsrock.member.dto.service.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/clothing")
@RequiredArgsConstructor
public class ClothingController {

    private final ClothingFeedbackService clothingFeedbackService;

    @PostMapping
    public ResponseEntity<Void> receiveFeedback(
            @AuthenticationPrincipal CapsrockUserDetails userDetails,
            @RequestBody ClothingFeedbackRequest clothingFeedbackRequest) {
        MemberInfoDTO memberInfoDTO = userDetails.getMemberInfoDTO();

        clothingFeedbackService.saveFeedback(memberInfoDTO, clothingFeedbackRequest);
        return ResponseEntity.ok().build();
    }

}
