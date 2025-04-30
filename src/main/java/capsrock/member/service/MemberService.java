package capsrock.member.service;


import capsrock.dto.request.ClothingFeedbackRequest;
import capsrock.member.dto.MemberInfoDTO;
import capsrock.member.dto.request.LoginRequest;
import capsrock.member.dto.request.RegisterRequest;
import capsrock.member.exception.MemberNotFoundException;
import capsrock.member.model.entity.Member;
import capsrock.member.model.vo.Email;
import capsrock.member.model.vo.EncryptedPassword;
import capsrock.member.model.vo.PlainPassword;
import capsrock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberInfoDTO login(LoginRequest loginRequest) {

        Email email = new Email(loginRequest.email());
        PlainPassword plainPassword = new PlainPassword(loginRequest.password());
        EncryptedPassword encryptedPassword = new EncryptedPassword(
                passwordEncoder.encode(plainPassword.value()));

        Member loginedMember = memberRepository.findByEmailAndEncryptedPassword(email, encryptedPassword)
                .orElseThrow(() -> new MemberNotFoundException(
                        "email이 %s인 회원을 찾지 못했습니다.".formatted(email.value())));

        return MemberInfoDTO.from(loginedMember);
    }

    @Transactional
    public MemberInfoDTO register(RegisterRequest registerRequest) {
        PlainPassword plainPassword = new PlainPassword(registerRequest.password());

        Member newMember = Member
                .builder()
                .email(registerRequest.email())
                .encryptedPassword(passwordEncoder.encode(plainPassword.value()))
                .nickname(registerRequest.nickname()).build();

        memberRepository.save(newMember);

        return MemberInfoDTO.from(newMember);
    }

    @Transactional(readOnly = true)
    public MemberInfoDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(id)));

        return MemberInfoDTO.from(member);
    }

    @Transactional
    public void saveClothingFeedback(Long memberId,
            ClothingFeedbackRequest clothingFeedbackRequest) {
    }
}
