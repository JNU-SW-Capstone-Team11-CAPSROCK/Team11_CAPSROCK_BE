package capsrock.clothing.controller;

import capsrock.clothing.dto.request.ClothingFeedbackRequest;
import capsrock.clothing.dto.request.ClothingPageRequest;
import capsrock.clothing.dto.response.ClothingResponse;
import capsrock.clothing.service.ClothingFeedbackService;
import capsrock.clothing.service.ClothingRecommendationService;
import capsrock.common.security.dto.CapsrockUserDetails;
import capsrock.member.dto.service.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clothing")
@RequiredArgsConstructor
public class ClothingController {

    private final ClothingFeedbackService clothingFeedbackService;
    private final ClothingRecommendationService clothingRecommendationService;

    @PostMapping
    public ResponseEntity<Void> receiveFeedback(
            @AuthenticationPrincipal CapsrockUserDetails userDetails,
            @RequestBody ClothingFeedbackRequest clothingFeedbackRequest) {
        MemberInfoDTO memberInfoDTO = userDetails.getMemberInfoDTO();

        clothingFeedbackService.saveFeedback(memberInfoDTO, clothingFeedbackRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ClothingResponse> getClothingPage(
            @AuthenticationPrincipal CapsrockUserDetails userDetails,
            ClothingPageRequest clothingPageRequest
    ) {
        MemberInfoDTO memberInfoDTO = userDetails.getMemberInfoDTO();

        return ResponseEntity.ok(clothingRecommendationService.recommendClothing(memberInfoDTO,
                clothingPageRequest));
    }

}
