package org.talky.platform.app.api.v1.request;

public record LoginRequest(
        String loginId,
        String password
) {
}
