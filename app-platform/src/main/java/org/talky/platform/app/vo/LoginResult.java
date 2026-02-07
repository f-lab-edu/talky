package org.talky.platform.app.vo;

import org.talky.auth.AccessToken;
import org.talky.auth.RefreshToken;

public record LoginResult(
        User user,
        AccessToken accessToken,
        RefreshToken refreshToken
) {
}
