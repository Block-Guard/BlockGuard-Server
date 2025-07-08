package com.blockguard.server.domain.user.dto.request;

import com.blockguard.server.domain.user.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String name;
    private String password;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;

}
