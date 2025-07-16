package com.blockguard.server.domain.guardian.dto.response;

import com.blockguard.server.domain.guardian.domain.Guardian;
import com.blockguard.server.global.config.S3.S3Service;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonIgnoreProperties("primary") // 자동으로 생성되는 "primary" 프로퍼티 무시
public class GuardianResponse {
    private Long guardiansId;
    private String name;

    @Schema(description = "전화번호 (010-1234-5678 형식)", example = "010-1234-5678")
    private String phoneNumber;

    @JsonProperty("isPrimary")
    private boolean isPrimary;
    private String profileImageUrl;
    private String createdAt;

    public static GuardianResponse from(Guardian guardian, S3Service s3Service) {
        return GuardianResponse.builder()
                .guardiansId(guardian.getId())
                .name(guardian.getName())
                .phoneNumber(guardian.getPhoneNumber())
                .isPrimary(guardian.isPrimary())
                .createdAt(guardian.getCreatedAt().toString())
                .profileImageUrl(
                        guardian.getProfileImageKey() != null
                                ? s3Service.getPublicUrl(guardian.getProfileImageKey())
                                : null
                )
                .build();
    }
}
