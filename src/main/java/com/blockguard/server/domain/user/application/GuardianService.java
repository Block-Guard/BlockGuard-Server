package com.blockguard.server.domain.user.application;

import com.blockguard.server.domain.auth.infra.JwtTokenProvider;
import com.blockguard.server.domain.guardian.dao.GuardianRepository;
import com.blockguard.server.domain.guardian.dto.response.GuardianResponse;
import com.blockguard.server.domain.guardian.dto.response.GuardiansListResponse;
import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public GuardiansListResponse getGuardiansListByUser(String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_INFO_NOT_FOUND));

        return GuardiansListResponse.builder()
                .guardians(
                        guardianRepository.findAllByUserId(user.getId()).stream()
                                .map(guardian -> GuardianResponse.builder()
                                        .guardiansId(guardian.getId())
                                        .name(guardian.getName())
                                        .phoneNumber(guardian.getPhoneNumber())
                                        .isPrimary(guardian.isPrimary())
                                        .createdAt(guardian.getCreatedAt().toString())
                                        .profileImageUrl(guardian.getProfileImageKey()) //Todo: s3 로직
                                        .build()
                                ).toList()
                        )
                .build();

    }
}
