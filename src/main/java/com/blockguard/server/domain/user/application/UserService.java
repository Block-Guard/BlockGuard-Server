package com.blockguard.server.domain.user.application;

import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.UpdatePasswordRequest;
import com.blockguard.server.domain.user.dto.request.UpdateUserInfo;
import com.blockguard.server.domain.user.dto.response.MyPageResponse;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.config.S3.S3Service;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_USER));

        user.markDeleted();
    }

    @Transactional
    public void updateUserInfo(Long id, UpdateUserInfo updateUserInfo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_USER));

        if (updateUserInfo.getName() != null) user.updateName(updateUserInfo.getName());

        if (updateUserInfo.getPhoneNumber() != null) user.updatePhoneNumber(updateUserInfo.getPhoneNumber());

        if (updateUserInfo.getBirthDate() != null) {
            try {
                LocalDate parsedBirthDate = LocalDate.parse(updateUserInfo.getBirthDate(), DateTimeFormatter.BASIC_ISO_DATE);
                user.updateBirthDate(parsedBirthDate);
            } catch (DateTimeParseException e){
                throw new BusinessExceptionHandler(ErrorCode.INVALID_DATE_FORMAT);
            }
        }
        if (updateUserInfo.getProfileImage() != null && !updateUserInfo.getProfileImage().isEmpty()) {
            String contentType = updateUserInfo.getProfileImage().getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_PROFILE_IMAGE);
             }
            String imageKey = s3Service.upload(updateUserInfo.getProfileImage(), "profiles");
            user.updateProfileImageKey(imageKey);
        }
    }

    public MyPageResponse getMyPageInfo(User user) {
        String profileImageUrl = (user.getProfileImageKey() != null) ?
                s3Service.getPublicUrl(user.getProfileImageKey()) : null;
        return MyPageResponse.from(user, profileImageUrl);
    }

    @Transactional
    public void updatePassword(User user, UpdatePasswordRequest updatePasswordRequest) {
        if(!passwordEncoder.matches(updatePasswordRequest.getCurrentPwd(), user.getPassword())){
            throw new BusinessExceptionHandler(ErrorCode.PASSWORD_MISMATCH);
        }

        String encodedNewPwd = passwordEncoder.encode(updatePasswordRequest.getNewPwd());
        user.changePassword(encodedNewPwd);
        userRepository.save(user);
    }
}
