package com.blockguard.server.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class UpdateUserInfo {
    @NotBlank
    private String name;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    @Schema(description = "생년월일 yyyyMMdd", example = "20000101")
    private String birthDate; //yyyyMMDD

    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$",
            message = "phoneNumber must match 010-1234-5678 format")
    private String phoneNumber;

    private MultipartFile profileImage;

}
