package org.talky.platform.app.api.v1.response;

import org.talky.platform.app.vo.TokenIssueResult;

public record RefreshResponse(
        String accessToken,
        String refreshToken
) {
    public static RefreshResponse from(TokenIssueResult result) {
        return new RefreshResponse(
                result.accessToken().tokenValue(),
                result.refreshToken().tokenValue()
        );
    }
}
