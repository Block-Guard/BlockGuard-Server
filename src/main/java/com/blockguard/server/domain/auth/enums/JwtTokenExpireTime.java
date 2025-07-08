package com.blockguard.server.domain.auth.enums;

import lombok.Getter;

@Getter
public enum JwtTokenExpireTime {
    ACCESS_TOKEN_EXPIRE_TIME(86400000),
    REFRESH_TOKEN_EXPIRE_TIME(86400000);

    private final long expireTime;

    JwtTokenExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

}
