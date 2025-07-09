package com.blockguard.server.domain.user.dto.response;

import com.blockguard.server.domain.auth.domain.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;
    private JwtToken jwtToken;
}
