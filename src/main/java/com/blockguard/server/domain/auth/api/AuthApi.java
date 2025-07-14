package com.blockguard.server.domain.auth.api;

import com.blockguard.server.domain.auth.application.AuthService;
import com.blockguard.server.domain.user.dto.request.FindEmailRequest;
import com.blockguard.server.domain.user.dto.request.FindPasswordRequest;
import com.blockguard.server.domain.user.dto.request.LoginRequest;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
import com.blockguard.server.domain.user.dto.response.FindEmailResponse;
import com.blockguard.server.domain.user.dto.response.LoginResponse;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "회원가입")
    @CustomExceptionDescription(SwaggerResponseDescription.REGISTER_FAIL)
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authService.register(registerRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.USER_REGISTERED, registerResponse));

    }


    @Operation(summary = "로그인")
    @CustomExceptionDescription(SwaggerResponseDescription.LOGIN_FAIL)
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.LOGIN_SUCCESS, loginResponse));
    }

    @Operation(summary = "아이디 찾기")
    @CustomExceptionDescription(SwaggerResponseDescription.FIND_EMAIL_FAIL)
    @PostMapping("find-email")
    public ResponseEntity<BaseResponse<FindEmailResponse>> findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest){
        FindEmailResponse findEmailResponse = authService.findEmail(findEmailRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.USER_EMAIL_FOUND, findEmailResponse));
    }

}
