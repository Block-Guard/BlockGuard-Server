package com.blockguard.server.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {
    private String currentPwd;
    private String newPwd;
}
