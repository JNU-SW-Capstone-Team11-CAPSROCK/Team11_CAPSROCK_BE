package capsrock.auth.controller;

import capsrock.auth.dto.response.TokenResponse;
import capsrock.auth.service.AuthService;
import capsrock.auth.LoginRequest;
import capsrock.auth.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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
