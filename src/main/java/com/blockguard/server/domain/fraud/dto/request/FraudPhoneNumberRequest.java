package com.blockguard.server.domain.fraud.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FraudPhoneNumberRequest {
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;
}
