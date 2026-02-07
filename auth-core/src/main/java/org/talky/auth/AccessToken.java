package org.talky.auth;

public record AccessToken(
        String jti,
        Long userId,
        UserRole role,
        String tokenValue
) {
}
