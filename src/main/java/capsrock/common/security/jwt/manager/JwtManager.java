package capsrock.common.security.jwt.manager;

import capsrock.member.dto.service.MemberInfoDTO;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {

    private final static Integer HOUR = 3600;
    private final static Integer EXPIRED_MILLIS = 1000 * 6 * HOUR;

    @Value("${jwt-secret-key}")
    private String jwtSecretKey;

    private SecretKey key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public Long extractId(String token) {
        return Long.valueOf(jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }


    public String generateToken(MemberInfoDTO memberInfoDTO) {
        return Jwts.builder()
                .subject(memberInfoDTO.id().toString())
                .claim("email", memberInfoDTO.email().value())
                .claim("nickname", memberInfoDTO.nickname().value())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRED_MILLIS))
                .signWith(this.key)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token); // 서명 검증
            return !isTokenExpired(token); // 만료 여부 확인
        } catch (JwtException e) {
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        return jwtParser.parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
}