package org.talky.platform.app.api.v1.response;

import org.talky.platform.app.vo.User;

public record MyInfoResponse(
        String loginId,
        String nickname,
        String userTag
) {
    public static MyInfoResponse from(User user) {
        return new MyInfoResponse(
                user.loginId(),
                user.nickname(),
                user.userTag()
        );
    }
}
