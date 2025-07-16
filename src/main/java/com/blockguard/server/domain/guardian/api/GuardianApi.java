package com.blockguard.server.domain.guardian.api;

import com.blockguard.server.domain.guardian.dto.request.CreateGuardianRequest;
import com.blockguard.server.domain.guardian.dto.response.GuardianResponse;
import com.blockguard.server.domain.guardian.dto.response.GuardiansListResponse;
import com.blockguard.server.domain.guardian.application.GuardianService;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.resolver.CurrentUser;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public ResponseEntity<BaseResponse<GuardiansListResponse>> getGuardiansList(@Parameter(hidden = true) @CurrentUser User user) {
        GuardiansListResponse guardiansList = guardianService.getGuardiansList(user);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIANS_FOUND, guardiansList));
    }

    @Operation(summary = "보호자 등록")
    @CustomExceptionDescription(SwaggerResponseDescription.FIND_GUARDIANS_FAIL)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<GuardianResponse>> createGuardian(@Parameter(hidden = true) @CurrentUser User user,
                                                                         @Valid CreateGuardianRequest createGuardianRequest) {
        GuardianResponse guardianResponse = guardianService.createGuardian(user, createGuardianRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.CREATE_GUARDIAN_SUCCESS, guardianResponse));
    }


}
