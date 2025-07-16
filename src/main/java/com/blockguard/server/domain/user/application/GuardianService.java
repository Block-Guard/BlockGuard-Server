package com.blockguard.server.domain.user.application;

import com.blockguard.server.domain.guardian.dao.GuardianRepository;
import com.blockguard.server.domain.guardian.dto.response.GuardianResponse;
import com.blockguard.server.domain.guardian.dto.response.GuardiansListResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.config.S3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final S3Service s3Service;

    @Transactional
    public GuardiansListResponse getGuardiansList(User user) {
        return GuardiansListResponse.builder()
                .guardians(
                        guardianRepository.findAllByUserId(user.getId()).stream()
                                .map(guardian ->
                                        GuardianResponse.builder()
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
                                        .build()
                                ).toList()
                )
                .build();

    }
}
