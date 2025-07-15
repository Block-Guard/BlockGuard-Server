package com.blockguard.server.domain.user.application;

import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.UpdateUserInfo;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new BusinessExceptionHandler(ErrorCode.INVALID_USER));

        user.markDeleted();
    }

    @Transactional
    public void updateUserInfo(Long id, UpdateUserInfo updateUserInfo) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new BusinessExceptionHandler(ErrorCode.INVALID_USER));

        if(updateUserInfo.getName()!=null) user.updateName(updateUserInfo.getName());
        if(updateUserInfo.getPhoneNumber()!=null) user.updatePhoneNumber(updateUserInfo.getPhoneNumber());
        // if(updateUserInfo.getProfileImage()!=null) user.updateProfileImageKey(updateUserInfo.getProfileImage());
    }
}
