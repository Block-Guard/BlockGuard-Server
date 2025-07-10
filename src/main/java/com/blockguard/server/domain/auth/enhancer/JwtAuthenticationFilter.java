package com.blockguard.server.domain.auth.enhancer;

import com.blockguard.server.domain.auth.infra.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import static com.blockguard.server.domain.auth.enums.JwtGrantType.GRANT_TYPE_ADMIN;
import static com.blockguard.server.domain.auth.enums.JwtGrantType.GRANT_TYPE_USER;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        // actuator 등 인증 없이 접근 가능한 경로는 필터 통과
        if (uri.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) &&
                ((bearerToken.startsWith(GRANT_TYPE_USER.getValue())) || (bearerToken.startsWith(GRANT_TYPE_ADMIN.getValue())))
        ) {
            return bearerToken.substring(7);
        }
        return null;
    }
}