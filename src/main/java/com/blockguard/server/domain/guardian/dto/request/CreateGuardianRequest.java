package com.blockguard.server.domain.guardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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

    // 선택 필드
    private MultipartFile profileImage;
}
