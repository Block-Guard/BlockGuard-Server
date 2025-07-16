package com.blockguard.server.domain.guardian.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class CreateGuardianRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "010-1234-5678 형식이어야 합니다.")
    private String phoneNumber;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile profileImage;
}
