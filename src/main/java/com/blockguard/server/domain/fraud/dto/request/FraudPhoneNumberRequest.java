package com.blockguard.server.domain.fraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FraudPhoneNumberRequest {
    // Todo: 전화번호 형식 논의 후 수정 -> 국제 번호 또는 지역 번호 고려
    @NotBlank
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "phoneNumber must match 010-1234-5678 format")
    private String phoneNumber;
}
