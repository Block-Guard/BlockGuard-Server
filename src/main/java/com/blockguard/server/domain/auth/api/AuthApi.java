package com.blockguard.server.domain.auth.api;

import com.blockguard.server.domain.auth.application.AuthService;
import com.blockguard.server.domain.user.dto.request.LoginRequest;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
import com.blockguard.server.domain.user.dto.response.LoginResponse;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
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
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authService.register(registerRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.USER_REGISTERED, registerResponse));

    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> register(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.LOGIN_SUCCESS, loginResponse));
    }

}
