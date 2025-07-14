package com.blockguard.server.domain.user.dto.request;

import com.blockguard.server.domain.user.domain.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate birthDate;

    private Gender gender;

    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$",
            message = "phoneNumber must match 010-1234-5678 format")
    private String phoneNumber;

}
