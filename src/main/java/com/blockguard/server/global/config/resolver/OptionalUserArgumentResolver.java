package com.blockguard.server.global.config.resolver;

import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class OptionalUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OptionalUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        // 현재 로그인된 사용자의 Authentication 정보 꺼내옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인되지 않은 상태라면
        if (authentication == null) {
            return null;
        }
        // 인증되지 않은 경우 에러 반환
        if (!(authentication.getPrincipal() instanceof User)) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_TOKEN);
        }
        // 인증된 사용자만 User 객체 반환
        return authentication.getPrincipal(); // 로그인된 사용자
    }
}
