package com.blockguard.server.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class UpdateUserInfo {
    private String name;
    private String birthDate; //yyyyMMDD
    private String phoneNumber;
    private MultipartFile profileImage;

}
