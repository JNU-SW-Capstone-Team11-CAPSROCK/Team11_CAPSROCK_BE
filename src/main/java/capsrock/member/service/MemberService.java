package capsrock.member.service;


import capsrock.dto.request.ClothingFeedbackRequest;
import capsrock.member.dto.MemberLoginDTO;
import capsrock.member.model.entity.Member;
import capsrock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void login(MemberLoginDTO memberLoginDTO) {
//        Member member = memberRepository.findByEmail(new Email(memberLoginDTO.email()));
//        return MemberDTO.from(member);
    }

    @Transactional
    public void saveClothingFeedback(Long memberId, ClothingFeedbackRequest clothingFeedbackRequest) {

    }
}
