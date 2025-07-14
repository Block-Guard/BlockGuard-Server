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


    /**
     * Authenticates a user with the provided login credentials.
     *
     * @param loginRequest the login credentials submitted by the user
     * @return a response containing authentication details and a success code if login is successful
     */
    @Operation(summary = "로그인")
    @CustomExceptionDescription(SwaggerResponseDescription.LOGIN_FAIL)
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.LOGIN_SUCCESS, loginResponse));
    }

    /**
     * Handles a request to find a user's email based on provided identifying information.
     *
     * @param findEmailRequest the request containing information needed to locate the user's email
     * @return a response entity containing the found email information and a success code
     */
    @Operation(summary = "아이디 찾기")
    @CustomExceptionDescription(SwaggerResponseDescription.FIND_EMAIL_FAIL)
    @PostMapping("find-email")
    public ResponseEntity<BaseResponse<FindEmailResponse>> findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest){
        FindEmailResponse findEmailResponse = authService.findEmail(findEmailRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.USER_EMAIL_FOUND, findEmailResponse));
    }

    /**
     * Sends a temporary password to the user's email address if the provided information is valid.
     *
     * @param findPasswordRequest the request containing user information for password recovery
     * @return a response indicating that a temporary password has been sent by email
     */
    @Operation(summary = "비밀번호 찾기", description = "이메일이 유효하면 해당 이메일로 임시 비밀번호를 발송합니다.")
    @CustomExceptionDescription(SwaggerResponseDescription.FIND_PASSWORD_FAIL)
    @PostMapping("find-password")
    public ResponseEntity<BaseResponse<Void>> findPassword(@RequestBody @Valid FindPasswordRequest findPasswordRequest){
        authService.sendTempPassword(findPasswordRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.SEND_TEMP_PASSWORD_BY_EMAIL));
    }

}
