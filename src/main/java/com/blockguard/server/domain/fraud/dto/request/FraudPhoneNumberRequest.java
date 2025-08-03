package com.blockguard.server.domain.fraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FraudPhoneNumberRequest {
    @NotBlank
    private String phoneNumber;
}
