package org.talky.platform.app.api.v1.response;

public record UserProfileResponse(
        String nickname,
        String userTag,
        String profileMessage
) {
}
