package com.blockguard.server.domain.auth.infra;

import com.blockguard.server.domain.auth.enums.JwtTokenExpireTime;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blockguard.server.domain.auth.domain.JwtToken;

@Component
public class JwtTokenGenerator {
    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenGenerator(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // User 정보를 가지고 AccessToken, RefreshToken을 생성
    public JwtToken generateToken(Long userId, String grantType) {
        // 권한 가져오기
        long now = (new Date()).getTime();

        // Jwt token time
        long accessTokenExpireTime = JwtTokenExpireTime.ACCESS_TOKEN_EXPIRE_TIME.getExpireTime();
        long refreshTokenExpireTime = JwtTokenExpireTime.REFRESH_TOKEN_EXPIRE_TIME.getExpireTime();


        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpireTime);
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("auth", "ROLE_USER")
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenExpireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}