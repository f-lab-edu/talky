package org.talky.platform.app.api.v1.request;

public record RefreshRequest(
        String accessToken,
        String refreshToken
) {
}
