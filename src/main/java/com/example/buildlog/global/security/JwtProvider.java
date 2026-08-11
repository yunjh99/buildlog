package com.example.buildlog.global.security;

import com.example.buildlog.global.filter.CustomUserDetails;
import com.example.buildlog.user.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final Key secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(String loginId, Role role) {
        return createToken(loginId, role, ACCESS, accessTokenExpiration);
    }

    public String createRefreshToken(String loginId, Role role) {
        return createToken(loginId, role, REFRESH, refreshTokenExpiration);
    }

    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, ACCESS);
    }

    public boolean isRefreshTokenValid(String token) {
        return isTokenValid(token, REFRESH);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parse(accessToken);
        CustomUserDetails principal = new CustomUserDetails(
                claims.getSubject(), Role.valueOf(claims.get("role", String.class))
        );
        return new UsernamePasswordAuthenticationToken(
                principal, accessToken, principal.getAuthorities()
        );
    }

    public String getLoginId(String token) {
        return parse(token).getSubject();
    }

    private String createToken(String loginId, Role role, String tokenType, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(loginId)
                .claim("role", role.name())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    private boolean isTokenValid(String token, String expectedType) {
        try {
            return expectedType.equals(parse(token).get(TOKEN_TYPE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody();
    }
}
