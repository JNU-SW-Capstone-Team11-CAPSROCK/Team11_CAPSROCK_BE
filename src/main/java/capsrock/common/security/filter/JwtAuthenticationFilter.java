package capsrock.common.security.filter;


import capsrock.common.security.dto.CapsrockUserDetails;
import capsrock.common.security.jwt.manager.JwtManager;
import capsrock.member.dto.MemberInfoDTO;
import capsrock.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //요청 당 한번만 활성화 되는 필터

    private final static String BEARER_TYPE = "Bearer ";

    private final JwtManager jwtManager; // 토큰 검증 클래스
    private final ApplicationContext applicationContext; //MemberService를 Lazy하게 가져와서 순환참조를 방지한다.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(BEARER_TYPE)) {
                filterChain.doFilter(request, response);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String token = authHeader.split(BEARER_TYPE)[1];

            if (token == null || !jwtManager.validateToken(token)) {
                filterChain.doFilter(request, response);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Long userId = jwtManager.extractId(token);

            MemberService memberService = applicationContext.getBean(MemberService.class);
            MemberInfoDTO memberInfo = memberService.getMemberById(userId);
            CapsrockUserDetails userDetails = new CapsrockUserDetails(memberInfo);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null /*이미 토큰인증 했기 때문에 null*/, null);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}
