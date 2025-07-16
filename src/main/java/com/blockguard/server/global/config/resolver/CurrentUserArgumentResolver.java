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
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUser가 붙어있고 타입이 User일 경우에만 처리
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        // 현재 로그인된 사용자의 Authentication 정보 꺼내옴
        // 로그인되어있다면 getPrincipal() 안에 User 객체 존재 -> 그대로 리턴해 컨트롤러에 주입
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||!(authentication.getPrincipal() instanceof User)){
            throw new BusinessExceptionHandler(ErrorCode.INVALID_TOKEN);
        }

        return authentication.getPrincipal();
    }
}
