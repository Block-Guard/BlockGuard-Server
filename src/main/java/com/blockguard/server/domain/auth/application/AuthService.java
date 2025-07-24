package com.blockguard.server.domain.auth.application;

import com.blockguard.server.domain.auth.domain.JwtToken;
import com.blockguard.server.domain.auth.infra.JwtTokenGenerator;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.*;
import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.dto.response.CheckEmailDuplicatedResponse;
import com.blockguard.server.domain.user.dto.response.FindEmailResponse;
import com.blockguard.server.domain.user.dto.response.LoginResponse;
import com.blockguard.server.domain.user.dto.response.RegisterResponse;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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


    public CheckEmailDuplicatedResponse checkEmailDuplicated(CheckEmailDuplicatedRequest checkEmailDuplicatedRequest) {
        boolean isDuplicated = userRepository.findByEmail(checkEmailDuplicatedRequest.getEmail()).isPresent();
        return CheckEmailDuplicatedResponse.builder()
                .isDuplicated(isDuplicated)
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

    public void sendTempPassword(FindPasswordRequest findPasswordRequest) {
        User user = userRepository.findByEmail(findPasswordRequest.getEmail())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.EMAIL_NOT_FOUND));

        String tempPassword = generateTempPwd(user);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(findPasswordRequest.getEmail());
        msg.setSubject("[BLOCKGUARD] 임시 비밀번호 안내");
        msg.setText("임시 비밀번호는 아래와 같습니다:\n\n" + tempPassword +
                "\n\n로그인 후 반드시 비밀번호를 변경해주세요." +
                "\n\n만약 비밀번호 재발급을 요청한 적이 없다면 반드시 고객센터로 문의해주십시오.");

        new Thread(() -> mailSender.send(msg)).start();

    }

    private JavaMailSender mailSender;

    private String generateTempPwd(User user) {
        String tempPassword = generateTempPassword();

        user.changePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        return tempPassword;
    }

    private String generateTempPassword() {
        String str = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_-+<>?";

        String all = str + digits + special;
        StringBuilder password = new StringBuilder();

        Random random = new SecureRandom();

        password.append(str.charAt(random.nextInt(str.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        for (int i = 0; i < 7; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        List<Character> passwordChars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(passwordChars);

        StringBuilder finalPassword = new StringBuilder();
        passwordChars.forEach(finalPassword::append);

        return finalPassword.toString();
    }

}
