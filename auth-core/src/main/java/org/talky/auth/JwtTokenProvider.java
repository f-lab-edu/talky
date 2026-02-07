package org.talky.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_REFRESH = "refresh";

    private static final String LOCAL_SECRET_KEY = "talky-local-dev-secret-key-that-is-at-least-32-bytes";
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRY_MS = 1000L * 60 * 30;        // 30분
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRY_MS = 1000L * 60 * 60 * 24 * 7; // 7일

    private final SecretKey secretKey;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    JwtTokenProvider(String secretKey, long accessTokenExpiryMs, long refreshTokenExpiryMs) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    public static JwtTokenProvider fromEnv() {
        String secretKey = System.getenv("JWT_SECRET_KEY");
        String accessExpiry = System.getenv("JWT_ACCESS_TOKEN_EXPIRY");
        String refreshExpiry = System.getenv("JWT_REFRESH_TOKEN_EXPIRY");

        secretKey = secretKey != null ? secretKey : LOCAL_SECRET_KEY;
        long accessTokenExpiryMs = accessExpiry != null ? Long.parseLong(accessExpiry): DEFAULT_ACCESS_TOKEN_EXPIRY_MS;
        long refreshTokenExpiryMs = refreshExpiry != null ? Long.parseLong(refreshExpiry) : DEFAULT_REFRESH_TOKEN_EXPIRY_MS;

        return new JwtTokenProvider(secretKey, accessTokenExpiryMs, refreshTokenExpiryMs);
    }

    public AccessToken createAccessToken(Long userId, UserRole role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiryMs);
        String jti = UUID.randomUUID().toString();

        String tokenValue = Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

        return new AccessToken(jti, userId, role, tokenValue);
    }

    public RefreshToken createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiryMs);
        String jti = UUID.randomUUID().toString();

        String tokenValue = Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

        LocalDateTime expiresAt = expiry.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return new RefreshToken(jti, userId, tokenValue, expiresAt);
    }

    public AccessToken parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get(CLAIM_TYPE, String.class);
        if (TYPE_REFRESH.equals(type)) {
            throw new InvalidTokenException("Refresh token cannot be used as access token");
        }

        String role = claims.get(CLAIM_ROLE, String.class);
        if (role == null) {
            throw new InvalidTokenException("Missing role claim in access token");
        }

        String jti = claims.getId();
        Long userId = Long.valueOf(claims.getSubject());
        return new AccessToken(jti, userId, UserRole.valueOf(role), token);
    }

    public Long parseRefreshToken(String token) {
        Claims claims = parseClaims(token);

        String type = claims.get(CLAIM_TYPE, String.class);
        if (!TYPE_REFRESH.equals(type)) {
            throw new InvalidTokenException("Access token cannot be used as refresh token");
        }

        return Long.valueOf(claims.getSubject());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid or expired token", e);
        }
    }
}
