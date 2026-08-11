package com.example.buildlog.auth.service;

import com.example.buildlog.auth.dto.LoginRequest;
import com.example.buildlog.global.security.AccessToken;
import com.example.buildlog.global.security.JwtProvider;
import com.example.buildlog.global.security.RefreshToken;
import com.example.buildlog.global.security.Token;
import com.example.buildlog.user.domain.User;
import com.example.buildlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public Token login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(AuthService::badCredentials);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw badCredentials();
        }
        return issueToken(user);
    }

    @Transactional(readOnly = true)
    public Token refresh(String refreshToken) {
        if (!jwtProvider.isRefreshTokenValid(refreshToken)) {
            throw new BadCredentialsException("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }
        User user = userRepository.findByLoginId(jwtProvider.getLoginId(refreshToken))
                .orElseThrow(AuthService::badCredentials);
        return issueToken(user);
    }

    private Token issueToken(User user) {
        return Token.builder()
                .accessToken(AccessToken.builder()
                        .header("Bearer")
                        .data(jwtProvider.createAccessToken(user.getLoginId(), user.getRole()))
                        .build())
                .refreshToken(RefreshToken.builder()
                        .header("REFRESH_TOKEN")
                        .data(jwtProvider.createRefreshToken(user.getLoginId(), user.getRole()))
                        .build())
                .build();
    }

    private static BadCredentialsException badCredentials() {
        return new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
