package com.blockguard.server.domain.auth.application;

import com.blockguard.server.domain.auth.domain.JwtToken;
import com.blockguard.server.domain.auth.infra.JwtTokenGenerator;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.LoginRequest;
import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
import com.blockguard.server.domain.user.dto.response.LoginResponse;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
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
            throw new IllegalArgumentException("이미 가입되어 있는 아이디입니다.");
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
                .msg("회원가입에 성공하였습니다.")
                .userId(user.getId())
                .build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다. 다시 한 번 확인해주세요."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        JwtToken jwtToken = jwtTokenGenerator.generateToken(user.getId(), "Bearer");

        return LoginResponse.builder()
                .msg("로그인에 성공하였습니다.")
                .userId(user.getId())
                .jwtToken(jwtToken)
                .build();

    }
}
