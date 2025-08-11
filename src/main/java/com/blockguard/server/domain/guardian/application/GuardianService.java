package com.blockguard.server.domain.guardian.application;

import com.blockguard.server.domain.guardian.dao.GuardianRepository;
import com.blockguard.server.domain.guardian.domain.Guardian;
import com.blockguard.server.domain.guardian.dto.request.CreateGuardianRequest;
import com.blockguard.server.domain.guardian.dto.request.UpdateGuardianPrimaryRequest;
import com.blockguard.server.domain.guardian.dto.request.UpdateGuardianRequest;
import com.blockguard.server.domain.guardian.dto.response.GuardianResponse;
import com.blockguard.server.domain.guardian.dto.response.GuardiansListResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.config.S3.S3Service;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
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
                                .map(guardian -> GuardianResponse.from(guardian, s3Service))
                                .toList()
                )
                .build();

    }

    @Transactional
    public GuardianResponse createGuardian(User user, CreateGuardianRequest request) {
        if (guardianRepository.existsByUserAndNameAndDeletedAtIsNull(user, request.getName())) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_GUARDIAN_NAME);
        }

        String key = (request.getProfileImage() != null && !request.getProfileImage().isEmpty())
                ? s3Service.upload(request.getProfileImage(), "guardians"): null;

        Guardian newGuardian = Guardian.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .profileImageKey(key)
                .user(user)
                .build();

        Guardian saved = guardianRepository.save(newGuardian);

        return GuardianResponse.from(saved, s3Service);
    }

    @Transactional
    public GuardianResponse updateGuardian(User user, Long guardianId, UpdateGuardianRequest request) {
        Guardian guardian = guardianRepository.findByIdAndUser(guardianId, user)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.GUARDIAN_NOT_FOUND));

        if (!guardian.getName().equals(request.getName()) &&
                guardianRepository.existsByUserAndNameAndDeletedAtIsNull(user, request.getName())) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_GUARDIAN_NAME);
        }

        String currentKey = guardian.getProfileImageKey();
        String nextKey = currentKey;

        // 기본 이미지로 변경하는 경우
        if (request.getIsDefaultImage() != null && request.getIsDefaultImage()) {
            nextKey = null;
            // 예전 이미지 삭제
            if (currentKey != null){
                s3Service.delete(currentKey);
            }
        }
        // 새 이미지로 변경하는 경우
        else if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            // 새 이미지 업로드
            nextKey = s3Service.upload(request.getProfileImage(), "guardians");
            // 예전 이미지 삭제
            if (currentKey != null){
                s3Service.delete(currentKey);
            }
        }
        guardian.updateGuardianInfo(request.getName(), request.getPhoneNumber(), nextKey);
        Guardian updated = guardianRepository.save(guardian);
        return GuardianResponse.from(updated, s3Service);
    }

    @Transactional
    public GuardianResponse updatePrimary(User user, Long guardianId, UpdateGuardianPrimaryRequest request) {
        Guardian guardian = guardianRepository.findByIdAndUser(guardianId, user)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.GUARDIAN_NOT_FOUND));

        // 다른 보호자 대표 해제
        if (request.isPrimary()) {
            guardianRepository.clearPrimaryFlagsByUser(user);
        }

        guardian.setPrimary(request.isPrimary());
        Guardian updated = guardianRepository.save(guardian);

        return GuardianResponse.from(updated, s3Service);
    }

    @Transactional
    public void deleteGuardian(User user, Long guardianId) {
        Guardian guardian = guardianRepository.findByIdAndUser(guardianId, user)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.GUARDIAN_NOT_FOUND));

        guardian.markDeleted();
    }
}
