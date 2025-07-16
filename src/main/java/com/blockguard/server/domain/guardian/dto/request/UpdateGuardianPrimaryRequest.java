package com.blockguard.server.domain.guardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateGuardianPrimaryRequest {
    @NotBlank
    private boolean isPrimary;
}
