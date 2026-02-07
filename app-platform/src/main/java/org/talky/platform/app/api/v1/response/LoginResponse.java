package org.talky.platform.app.api.v1.response;

import org.talky.platform.app.vo.LoginResult;
import org.talky.platform.app.vo.User;

public record LoginResponse(
        String loginId,
        String nickname,
        String userTag,
        String accessToken,
        String refreshToken
) {
    public static LoginResponse from(LoginResult result) {
        User user = result.user();
        return new LoginResponse(
                user.loginId(),
                user.nickname(),
                user.userTag(),
                result.accessToken().tokenValue(),
                result.refreshToken().tokenValue()
        );
    }
}
