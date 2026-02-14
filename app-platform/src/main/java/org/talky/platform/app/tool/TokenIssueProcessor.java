package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.RefreshToken;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.app.vo.TokenIssueResult;
import org.talky.platform.app.vo.User;

@Component
@RequiredArgsConstructor
public class TokenIssueProcessor {

    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSessionWriter loginSessionWriter;
    private final TsidGenerator tsidGenerator;

    public TokenIssueResult issue(User user, ClientInfo clientInfo) {
        AccessToken accessToken = jwtTokenProvider.createAccessToken(user.id(), user.role());
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(user.id());

        TokenIssueResult tokenIssueResult = new TokenIssueResult(user, accessToken, refreshToken);
        LoginSession session = LoginSession.of(tsidGenerator.generate(), tokenIssueResult, clientInfo);
        loginSessionWriter.save(session);

        return tokenIssueResult;
    }
}
