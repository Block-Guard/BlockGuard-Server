package com.blockguard.server.domain.user.application;

import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.domain.User;
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
}
