package capsrock.member.service;


import capsrock.auth.dto.request.LoginRequest;
import capsrock.auth.dto.request.RegisterRequest;
import capsrock.member.dto.service.MemberInfoDTO;
import capsrock.member.dto.service.RecentLocationDTO;
import capsrock.member.exception.MemberNotFoundException;
import capsrock.member.model.entity.Member;
import capsrock.member.model.vo.Email;
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

    @Transactional
    public MemberInfoDTO login(LoginRequest loginRequest) {
        Email email;
        PlainPassword plainPassword;
        try {
            email = new Email(loginRequest.email());
            plainPassword = new PlainPassword(loginRequest.password());
        } catch (IllegalArgumentException e) {
            throw new MemberNotFoundException(
                    "email이 %s인 회원을 찾지 못했습니다.".formatted(loginRequest.email()));
        }

        Member foundMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(
                        "email이 %s인 회원을 찾지 못했습니다.".formatted(email.value())));

        if (!passwordEncoder.matches(plainPassword.value(),
                foundMember.getEncryptedPassword().value())) {
            throw new MemberNotFoundException("email이 %s인 회원을 찾지 못했습니다.".formatted(email.value()));
        }

        foundMember.updateLocation(loginRequest.latitude(), loginRequest.longitude());

        return MemberInfoDTO.from(foundMember);
    }

    @Transactional
    public MemberInfoDTO register(RegisterRequest registerRequest) {
        PlainPassword plainPassword = new PlainPassword(registerRequest.password());

        Member newMember = Member
                .builder()
                .email(registerRequest.email())
                .encryptedPassword(passwordEncoder.encode(plainPassword.value()))
                .nickname(registerRequest.nickname())
                .latitude(registerRequest.latitude())
                .longitude(registerRequest.longitude())
                .build();

        memberRepository.save(newMember);

        return MemberInfoDTO.from(newMember);
    }

    @Transactional(readOnly = true)
    public MemberInfoDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("id가 %d인 회원을 찾지 못했습니다.".formatted(id)));

        return MemberInfoDTO.from(member);
    }

    @Transactional(readOnly = true)
    public RecentLocationDTO getRecentLocationById(Long id) {
        MemberInfoDTO memberInfoDTO = getMemberById(id);

        return new RecentLocationDTO(memberInfoDTO.recentLocation().longitude(),
                memberInfoDTO.recentLocation().latitude());
    }
}
