package com.blockguard.server.domain.auth.api;

import com.blockguard.server.domain.auth.application.AuthService;
import com.blockguard.server.domain.user.dto.request.LoginRequest;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
import com.blockguard.server.domain.user.dto.response.LoginResponse;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse registerResponse = authService.register(registerRequest);
            return ResponseEntity.ok(registerResponse);
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> register(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
