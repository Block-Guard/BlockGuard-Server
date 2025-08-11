package com.blockguard.server.domain.user.dto.response;

import com.blockguard.server.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
public class MyPageResponse {
    private String email;
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String profileImageUrl;

    public static MyPageResponse from(User user, String profileImageUrl) {
        return MyPageResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .birthDate(user.getBirthDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
