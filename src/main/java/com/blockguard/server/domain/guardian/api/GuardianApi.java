package com.blockguard.server.domain.guardian.api;

import com.blockguard.server.domain.guardian.dto.response.GuardiansListResponse;
import com.blockguard.server.domain.user.application.GuardianService;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guardians")
public class GuardianApi {
    private final GuardianService guardianService;

    @Operation(summary = "보호자 목록 조회")
    @CustomExceptionDescription(SwaggerResponseDescription.FIND_GUARDIANS_FAIL)
    @GetMapping
    public ResponseEntity<BaseResponse<GuardiansListResponse>> getGuardiansListByUser(@RequestHeader("Authorization") String token) {
        GuardiansListResponse guardiansListByUser = guardianService.getGuardiansListByUser(token);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIANS_FOUND, guardiansListByUser));

    }
}
