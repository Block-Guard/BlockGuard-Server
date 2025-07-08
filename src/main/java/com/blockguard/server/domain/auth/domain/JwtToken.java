package com.blockguard.server.domain.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {
        private String grantType;
        private String accessToken;
        private String refreshToken;
    }

