package com.blockguard.server.domain.guardian.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GuardianResponse {
    private Long guardiansId;
    private String name;
    private String phoneNumber;
    private boolean isPrimary;
    private String profileImageUrl;
    private String createdAt;
}
