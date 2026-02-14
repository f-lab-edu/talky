package org.talky.platform.app.vo;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
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
    public static LoginSession of(Long id, TokenIssueResult result, ClientInfo clientInfo) {
        return LoginSession.builder()
                .id(id)
                .userId(result.user().id())
                .accessJti(result.accessToken().jti())
                .refreshJti(result.refreshToken().jti())
                .clientInfo(clientInfo)
                .expiresAt(result.refreshToken().expiresAt())
                .build();
    }

    public LoginSession revoke() {
        return toBuilder().revokedAt(LocalDateTime.now()).build();
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
