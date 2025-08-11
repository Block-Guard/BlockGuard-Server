package com.blockguard.server.domain.guardian.api;

import com.blockguard.server.domain.guardian.dto.request.CreateGuardianRequest;
import com.blockguard.server.domain.guardian.dto.request.UpdateGuardianPrimaryRequest;
import com.blockguard.server.domain.guardian.dto.request.UpdateGuardianRequest;
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
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIAN_LIST_RETRIEVED, guardiansList));
    }

    @Operation(summary = "보호자 등록")
    @CustomExceptionDescription(SwaggerResponseDescription.CREATE_GUARDIAN_FAIL)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<GuardianResponse>> createGuardian(@Parameter(hidden = true) @CurrentUser User user,
                                                                         @Valid CreateGuardianRequest createGuardianRequest) {
        GuardianResponse guardianResponse = guardianService.createGuardian(user, createGuardianRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIAN_REGISTERED, guardianResponse));
    }

    @Operation(summary = "보호자 정보 수정")
    @CustomExceptionDescription(SwaggerResponseDescription.UPDATE_GUARDIAN_FAIL)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<GuardianResponse>> updateGuardian(@Parameter(hidden = true) @CurrentUser User user,
                                                                         @PathVariable("id") Long guardianId,
                                                                         @Valid UpdateGuardianRequest updateGuardianRequest) {
        GuardianResponse guardianResponse = guardianService.updateGuardian(user, guardianId, updateGuardianRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIAN_UPDATED, guardianResponse));
    }

    @Operation(summary = "보호자 대표 여부 수정")
    @CustomExceptionDescription(SwaggerResponseDescription.UPDATE_GUARDIAN_PRIMARY_FAIL)
    @PatchMapping(value = "/{id}/primary")
    public ResponseEntity<BaseResponse<GuardianResponse>> updatePrimary(@Parameter(hidden = true) @CurrentUser User user,
                                                                         @PathVariable("id") Long guardianId,
                                                                         @Valid @RequestBody UpdateGuardianPrimaryRequest updateGuardianPrimaryRequest) {
        GuardianResponse guardianResponse = guardianService.updatePrimary(user, guardianId, updateGuardianPrimaryRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIAN_PRIMARY_UPDATED, guardianResponse));
    }

    @Operation(summary = "보호자 삭제")
    @CustomExceptionDescription(SwaggerResponseDescription.UPDATE_GUARDIAN_PRIMARY_FAIL)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteGuardian(@Parameter(hidden = true) @CurrentUser User user,
                                                                        @PathVariable("id") Long guardianId) {
        guardianService.deleteGuardian(user, guardianId);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.GUARDIAN_DELETED));
    }


}
