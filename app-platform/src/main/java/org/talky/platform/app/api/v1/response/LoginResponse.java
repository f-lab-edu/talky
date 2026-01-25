package org.talky.platform.app.api.v1.response;

public record LoginResponse(
        String loginId,
        String nickname,
        String userTag,
        String token
) {
}
