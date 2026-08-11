package com.example.buildlog.global.security;

import com.example.buildlog.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {
    private final JwtProvider jwtProvider = new JwtProvider(
            "test-secret-key-for-jwt-must-be-at-least-32-bytes",
            60_000,
            120_000
    );

    @Test
    @DisplayName("Access Token과 Refresh Token의 용도를 구분해 검증한다")
    void validatesTokenType() {
        String accessToken = jwtProvider.createAccessToken("admin", Role.ADMIN);
        String refreshToken = jwtProvider.createRefreshToken("admin", Role.ADMIN);

        assertThat(jwtProvider.isAccessTokenValid(accessToken)).isTrue();
        assertThat(jwtProvider.isRefreshTokenValid(accessToken)).isFalse();
        assertThat(jwtProvider.isRefreshTokenValid(refreshToken)).isTrue();
        assertThat(jwtProvider.isAccessTokenValid(refreshToken)).isFalse();
    }

    @Test
    @DisplayName("Refresh Token에서 loginId를 추출한다")
    void extractsLoginIdFromRefreshToken() {
        String refreshToken = jwtProvider.createRefreshToken("admin", Role.ADMIN);

        assertThat(jwtProvider.getLoginId(refreshToken)).isEqualTo("admin");
    }

    @Test
    @DisplayName("Access Token으로 인증 객체를 생성한다")
    void createsAuthenticationFromAccessToken() {
        String accessToken = jwtProvider.createAccessToken("admin", Role.ADMIN);

        var authentication = jwtProvider.getAuthentication(accessToken);

        assertThat(authentication.getName()).isEqualTo("admin");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}
