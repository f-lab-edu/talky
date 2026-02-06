package org.talky.platform.app.api.v1.response;

import org.talky.platform.app.vo.User;

public record RegisterResponse(
        String loginId,
        String nickname,
        String userTag
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getLoginId(),
                user.getNickname(),
                user.getUserTag()
        );
    }
}
