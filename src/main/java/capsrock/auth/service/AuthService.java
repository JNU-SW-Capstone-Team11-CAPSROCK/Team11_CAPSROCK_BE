package capsrock.auth.service;

import capsrock.common.security.jwt.manager.JwtManager;
import capsrock.member.dto.service.MemberInfoDTO;
import capsrock.auth.dto.request.LoginRequest;
import capsrock.auth.dto.request.RegisterRequest;
import capsrock.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final JwtManager jwtManager;

    public String loginAndGenerateToken(LoginRequest loginRequest) {
        MemberInfoDTO loginMemberInfo = memberService.login(loginRequest);

        return jwtManager.generateToken(loginMemberInfo);
    }

    public String registerAndGenerateToken(RegisterRequest registerRequest) {
        MemberInfoDTO registerMemberInfo = memberService.register(registerRequest);

        return jwtManager.generateToken(registerMemberInfo);
    }
}
