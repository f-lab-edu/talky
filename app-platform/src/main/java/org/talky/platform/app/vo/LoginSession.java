package org.talky.platform.app.vo;

import jakarta.annotation.Nullable;
import lombok.Builder;

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
}
