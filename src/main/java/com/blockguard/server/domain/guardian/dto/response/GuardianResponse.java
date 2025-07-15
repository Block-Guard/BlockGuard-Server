package com.blockguard.server.domain.guardian.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GuardianResponse {
    private Long guardiansId;
    private String name;

    @Schema(description = "전화번호 (010-1234-5678 형식)", example = "010-1234-5678")
    private String phoneNumber;
    private boolean isPrimary;
    private String profileImageUrl;
    private String createdAt;
}
