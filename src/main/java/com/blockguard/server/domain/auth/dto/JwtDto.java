package com.blockguard.server.domain.auth.dto;

public record JwtDto(String accessToken, String refreshToken) {
}