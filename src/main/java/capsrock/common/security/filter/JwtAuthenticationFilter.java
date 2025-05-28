package capsrock.common.security.filter;


import static capsrock.common.constants.CommonConstants.CRITICAL_ERROR_MESSAGE;

import capsrock.common.security.dto.CapsrockUserDetails;
import capsrock.common.security.exception.handler.Rest401Handler;
import capsrock.common.security.exception.handler.Rest500Handler;
import capsrock.common.security.jwt.manager.JwtManager;
import capsrock.member.dto.service.MemberInfoDTO;
import capsrock.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String BEARER_TYPE = "Bearer ";
    private final JwtManager jwtManager;
    private final ApplicationContext applicationContext;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Rest401Handler rest401Handler;
    private final Rest500Handler rest500Handler;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !pathMatcher.match("/api/clothing/**", path);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_TYPE)) {
                throw new InsufficientAuthenticationException("Auth 헤더가 유효하지 않습니다.");
            }

            String token = authHeader.substring(BEARER_TYPE.length());
            if (!jwtManager.validateToken(token)) {
                throw new BadCredentialsException("토큰이 올바르지 않습니다.");
            }

            Long userId = jwtManager.extractId(token);
            MemberService memberService = applicationContext.getBean(MemberService.class);
            MemberInfoDTO memberInfo = memberService.getMemberById(userId);

            CapsrockUserDetails userDetails = new CapsrockUserDetails(memberInfo);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            rest401Handler.commence(request, response, ex);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            log.error(CRITICAL_ERROR_MESSAGE, ex);
            rest500Handler.commence(request, response,
                    new AuthenticationServiceException(CRITICAL_ERROR_MESSAGE, ex));
        }
    }
}