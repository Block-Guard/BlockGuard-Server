package com.blockguard.server.domain.auth.application;

import com.blockguard.server.domain.auth.domain.JwtToken;
import com.blockguard.server.domain.auth.infra.JwtTokenGenerator;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.FindEmailRequest;
import com.blockguard.server.domain.user.dto.request.LoginRequest;
import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
import com.blockguard.server.domain.user.dto.response.FindEmailResponse;
import com.blockguard.server.domain.user.dto.response.LoginResponse;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenGenerator jwtTokenGenerator;

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail((registerRequest.getEmail())).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATED_EMAIL);
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .name(registerRequest.getName())
                .birthDate(registerRequest.getBirthDate())
                .gender(registerRequest.getGender())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .userId(user.getId())
                .build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_EMAIL));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_PASSWORD);
        }

        JwtToken jwtToken = jwtTokenGenerator.generateToken(user.getId(), "Bearer");

        return LoginResponse.builder()
                .userId(user.getId())
                .jwtToken(jwtToken)
                .build();

    }

    public FindEmailResponse findEmail(FindEmailRequest findEmailRequest) {
        User user = userRepository.findByNameAndPhoneNumberAndBirthDate(
                        findEmailRequest.getName(), findEmailRequest.getPhoneNumber(), findEmailRequest.getBirthDate())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_INFO_NOT_FOUND));

        return FindEmailResponse.builder()
                .email(user.getEmail())
                .build();
    }
}
