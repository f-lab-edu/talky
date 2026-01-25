package org.talky.platform.app.api.v1.response;

public record MyInfoResponse(
        String loginId,
        String nickname,
        String userTag
) {
}
