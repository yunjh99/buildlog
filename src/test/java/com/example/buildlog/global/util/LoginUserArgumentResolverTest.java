package com.example.buildlog.global.util;

import com.example.buildlog.global.error.exception.InvalidLoginUserException;
import com.example.buildlog.global.filter.CustomUserDetails;
import com.example.buildlog.global.security.Login;
import com.example.buildlog.user.domain.Role;
import com.example.buildlog.user.dto.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginUserArgumentResolverTest {
    private final LoginUserArgumentResolver resolver = new LoginUserArgumentResolver();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("@Login이 붙은 LoginUser 파라미터를 지원한다")
    void supportsLoginUserParameter() throws Exception {
        assertThat(resolver.supportsParameter(parameter("loginUser"))).isTrue();
    }

    @Test
    @DisplayName("@Login이 없거나 타입이 다르면 지원하지 않는다")
    void rejectsUnsupportedParameter() throws Exception {
        assertThat(resolver.supportsParameter(parameter("withoutAnnotation"))).isFalse();
        assertThat(resolver.supportsParameter(parameter("wrongType"))).isFalse();
    }

    @Test
    @DisplayName("인증 정보에서 loginId와 역할을 추출한다")
    void resolvesAuthenticatedLoginUser() throws Exception {
        CustomUserDetails principal = new CustomUserDetails("admin", Role.ADMIN);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginUser result = (LoginUser) resolver.resolveArgument(
                parameter("loginUser"), null, null, null
        );

        assertThat(result.getLoginId()).isEqualTo("admin");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("인증 정보가 없으면 InvalidLoginUserException이 발생한다")
    void rejectsUnauthenticatedRequest() throws Exception {
        assertThatThrownBy(() -> resolver.resolveArgument(
                parameter("loginUser"), null, null, null
        )).isInstanceOf(InvalidLoginUserException.class);
    }

    private MethodParameter parameter(String methodName) throws Exception {
        Method method = Fixture.class.getDeclaredMethod(methodName,
                methodName.equals("wrongType") ? String.class : LoginUser.class);
        return new MethodParameter(method, 0);
    }

    private static class Fixture {
        void loginUser(@Login LoginUser loginUser) {}
        void withoutAnnotation(LoginUser loginUser) {}
        void wrongType(@Login String value) {}
    }
}
