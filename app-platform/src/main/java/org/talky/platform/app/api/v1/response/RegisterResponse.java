package org.talky.platform.app.api.v1.response;

public record RegisterResponse(
        String loginId,
        String nickname,
        String userTag
) {
}
