package com.example.buildlog.global.util;

import com.example.buildlog.global.error.exception.InvalidLoginUserException;
import com.example.buildlog.global.filter.CustomUserDetails;
import com.example.buildlog.global.security.Login;
import com.example.buildlog.user.domain.Role;
import com.example.buildlog.user.dto.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.stereotype.Component;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Login 어노테이션이 있는지 확인
        return parameter.hasParameterAnnotation(Login.class)
                && LoginUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null) throw new InvalidLoginUserException();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Role role = customUserDetails.getAuthorities().stream()
                .map(authority -> Role.ofValue(authority.getAuthority()))
                .findFirst()
                .orElseThrow(InvalidLoginUserException::new);

        return LoginUser.builder()
                .loginId(customUserDetails.getUsername())
                .role(role) // role을 설정
                .build();
    }
}
