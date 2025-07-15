package com.blockguard.server.domain.user.application;

import com.blockguard.server.domain.user.dao.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

}
