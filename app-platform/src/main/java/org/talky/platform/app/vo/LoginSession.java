package org.talky.platform.app.vo;

import jakarta.annotation.Nullable;
import lombok.Builder;
import org.talky.auth.AccessToken;
import org.talky.auth.RefreshToken;

import java.time.LocalDateTime;

@Builder
public record LoginSession(
        Long id,
        Long userId,
        String accessJti,
        String refreshJti,
        @Nullable LocalDateTime revokedAt, // 세션 폐기 시각 (리프레시 토큰을 이용한 재발급, 또는 로그아웃 시점)
        ClientInfo clientInfo,
        LocalDateTime expiresAt,
        @Nullable LocalDateTime createdAt
) {
    public static LoginSession of(Long id, Long userId, AccessToken accessToken, RefreshToken refreshToken, ClientInfo clientInfo) {
        return LoginSession.builder()
                .id(id)
                .userId(userId)
                .accessJti(accessToken.jti())
                .refreshJti(refreshToken.jti())
                .clientInfo(clientInfo)
                .expiresAt(refreshToken.expiresAt())
                .build();
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
