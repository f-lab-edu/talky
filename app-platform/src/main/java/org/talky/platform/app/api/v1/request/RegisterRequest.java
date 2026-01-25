package org.talky.platform.app.api.v1.request;

public record RegisterRequest(
        String loginId,
        String password,
        String nickname
) {
}
