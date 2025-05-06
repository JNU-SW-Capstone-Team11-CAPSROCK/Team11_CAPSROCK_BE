package capsrock.auth.controller;

import capsrock.auth.dto.response.TokenResponse;
import capsrock.auth.service.AuthService;
import capsrock.auth.dto.request.LoginRequest;
import capsrock.auth.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        String accessToken = authService.loginAndGenerateToken(loginRequest);
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest registerRequest) {
        String accessToken = authService.registerAndGenerateToken(registerRequest);
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }
}
