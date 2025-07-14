package com.blockguard.server.domain.auth.application;

import com.blockguard.server.domain.auth.domain.JwtToken;
import com.blockguard.server.domain.auth.infra.JwtTokenGenerator;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.FindEmailRequest;
import com.blockguard.server.domain.user.dto.request.FindPasswordRequest;
import com.blockguard.server.domain.user.dto.request.LoginRequest;
import com.blockguard.server.domain.user.dao.UserRepository;
import com.blockguard.server.domain.user.dto.request.RegisterRequest;
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

    /**
     * Authenticates a user with the provided login credentials and returns a JWT token upon successful authentication.
     *
     * @param loginRequest the login credentials containing email and password
     * @return a LoginResponse containing the user ID and a generated JWT token
     * @throws BusinessExceptionHandler if the email does not exist or the password is incorrect
     */
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

    /**
     * Retrieves a user's email address based on their name, phone number, and birth date.
     *
     * @param findEmailRequest the request containing the user's identifying information
     * @return a response containing the user's email address
     * @throws BusinessExceptionHandler if no user matching the provided information is found
     */
    public FindEmailResponse findEmail(FindEmailRequest findEmailRequest) {
        User user = userRepository.findByNameAndPhoneNumberAndBirthDate(
                        findEmailRequest.getName(), findEmailRequest.getPhoneNumber(), findEmailRequest.getBirthDate())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_INFO_NOT_FOUND));

        return FindEmailResponse.builder()
                .email(user.getEmail())
                .build();
    }

    /**
     * Sends a temporary password to the user's email address for password reset.
     *
     * If the user with the specified email does not exist, throws a {@code BusinessExceptionHandler} with {@code EMAIL_NOT_FOUND}.
     * The user's password is updated to a newly generated temporary password, which is sent via email asynchronously.
     *
     * @param findPasswordRequest the request containing the user's email for password reset
     */
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

    /**
     * Generates a secure temporary password for the given user, updates the user's password with its encoded value, and persists the change.
     *
     * @param user the user whose password will be reset
     * @return the plain temporary password generated for the user
     */
    private String generateTempPwd(User user) {
        String tempPassword = generateTempPassword();

        user.changePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        return tempPassword;
    }

    /**
     * Generates a secure random temporary password containing at least one lowercase letter, one digit, and one special character.
     *
     * The resulting password is 10 characters long and the character order is randomized.
     *
     * @return a randomly generated temporary password string
     */
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
