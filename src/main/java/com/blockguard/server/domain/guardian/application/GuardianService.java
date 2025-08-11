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
        // 입력 플래그/파일 상태
        final boolean wantDefault = Boolean.TRUE.equals(request.getIsDefaultImage());
        final boolean hasNewFile  = request.getProfileImage() != null && !request.getProfileImage().isEmpty();

        // 기본이미지로 변경 + 새 파일 업로드 동시 요청 불가
        if (wantDefault && hasNewFile) {
            throw new BusinessExceptionHandler(ErrorCode.UPDATE_PROFILE_CONFLICT);
        }

        String currentKey = guardian.getProfileImageKey();
        String nextKey = currentKey;

        // 기본 이미지로 변경하는 경우
        if (wantDefault) {
            nextKey = null;
            // 커밋 후 기존 파일 삭제, 롤백 시 아무것도 안 함
            registerAfterCommitDelete(currentKey, null);
        }
        // 새 이미지로 변경하는 경우
        else if (hasNewFile) {
            // 새 이미지 업로드
            String uploadedKey = s3Service.upload(request.getProfileImage(), "guardians");
            nextKey = uploadedKey;
            // 커밋 후 기존 파일 삭제, 롤백 시 방금 올린 파일 삭제
            registerAfterCommitDelete(currentKey, uploadedKey);
        }
        // 기존 이미지 유지 (nextKey = currentKey)
        guardian.updateGuardianInfo(request.getName(), request.getPhoneNumber(), nextKey);
        Guardian updated = guardianRepository.save(guardian);
        return GuardianResponse.from(updated, s3Service);
    }

    // 트랜잭션 커밋 후 기존 키 삭제, 롤백 시 새로 업로드한 키 삭제
    private void registerAfterCommitDelete(String oldKey, String uploadedKey) {
        org.springframework.transaction.support.TransactionSynchronizationManager
                .registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override public void afterCommit() {
                        if (oldKey != null && !oldKey.equals(uploadedKey)) {
                            s3Service.delete(oldKey);
                        }
                    }
                    @Override public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK && uploadedKey != null) {
                            s3Service.delete(uploadedKey);
                        }
                    }
                });
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
