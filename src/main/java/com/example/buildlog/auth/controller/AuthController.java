package com.example.buildlog.auth.controller;

import com.example.buildlog.auth.dto.LoginRequest;
import com.example.buildlog.auth.dto.LoginResponse;
import com.example.buildlog.auth.service.AuthService;
import com.example.buildlog.global.common.SuccessResponse;
import com.example.buildlog.global.security.Token;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    private final AuthService authService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public SuccessResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        Token token = authService.login(request);
        addRefreshCookie(response, token.getRefreshToken().getData());
        return tokenResponse("로그인 성공", token);
    }

    @PostMapping("/refresh")
    public SuccessResponse<LoginResponse> refresh(
            @CookieValue(REFRESH_TOKEN_COOKIE) String refreshToken,
            HttpServletResponse response
    ) {
        Token token = authService.refresh(refreshToken);
        addRefreshCookie(response, token.getRefreshToken().getData());
        return tokenResponse("토큰 재발급 성공", token);
    }

    @PostMapping("/logout")
    public SuccessResponse<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = cookieBuilder("").maxAge(Duration.ZERO).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return SuccessResponse.of(HttpStatus.OK, "로그아웃 성공");
    }

    private void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = cookieBuilder(refreshToken)
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private ResponseCookie.ResponseCookieBuilder cookieBuilder(String value) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/api/auth");
    }

    private SuccessResponse<LoginResponse> tokenResponse(String message, Token token) {
        return SuccessResponse.of(HttpStatus.OK, message,
                new LoginResponse(token.getAccessToken().getData(), token.getAccessToken().getHeader()));
    }
}
