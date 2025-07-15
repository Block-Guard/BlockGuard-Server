package com.blockguard.server.domain.guardian.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GuardiansListResponse {
    private List<GuardianResponse> guardians;
}
