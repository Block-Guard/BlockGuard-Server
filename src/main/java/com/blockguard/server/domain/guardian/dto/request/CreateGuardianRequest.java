package com.blockguard.server.domain.guardian.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class CreateGuardianRequest {
    @NotBlank
    @Size(max = 50, message = "name length must be <= 50")
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "phoneNumber must match 010-1234-5678 format")
    private String phoneNumber;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile profileImage;
}
