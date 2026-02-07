package org.talky.auth;

import java.time.LocalDateTime;

public record RefreshToken(
        String jti,
        Long userId,
        String tokenValue,
        LocalDateTime expiresAt
) {
}
