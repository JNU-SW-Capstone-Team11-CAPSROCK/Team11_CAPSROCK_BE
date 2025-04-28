package capsrock.clothing.feedback.service;

import capsrock.dto.request.ClothingFeedbackRequest;
import capsrock.member.dto.MemberInfoDTO;
import capsrock.member.service.MemberService;
import org.springframework.stereotype.Service;

@Service
public class ClothingFeedbackService {


    private final MemberService memberService;

    public ClothingFeedbackService(MemberService memberService) {
        this.memberService = memberService;
    }

    public void processFeedback(MemberInfoDTO memberInfoDTO, ClothingFeedbackRequest clothingFeedbackRequest) {
        memberService.saveClothingFeedback(memberInfoDTO.memberId(), clothingFeedbackRequest);
    }
}
