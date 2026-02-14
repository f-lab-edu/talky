package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.RefreshToken;
import org.talky.auth.UserStatus;
import org.talky.platform.app.tool.LoginSessionReader;
import org.talky.platform.app.tool.LoginSessionWriter;
import org.talky.platform.app.tool.TokenIssueProcessor;
import org.talky.platform.app.tool.UserReader;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.TokenIssueResult;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserReader userReader;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSessionReader loginSessionReader;
    private final LoginSessionWriter loginSessionWriter;
    private final TokenIssueProcessor tokenIssueProcessor;

    @Transactional
    public TokenIssueResult login(String loginId, String password, ClientInfo clientInfo) {
        User user = userReader.findByLoginId(loginId);

        if (!passwordEncoder.matches(password, user.password())) {
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        if (user.status() == UserStatus.BANNED) {
            throw new CoreException(ErrorCode.BANNED);
        }

        return tokenIssueProcessor.issue(user, clientInfo);
    }

    @Transactional
    public void logout(String accessToken) {
        AccessToken token = jwtTokenProvider.parseAccessToken(accessToken);
        LoginSession session = loginSessionReader.getByAccessJti(token.jti());
        session = session.revoke();
        loginSessionWriter.update(session);
    }

    @Transactional
    public TokenIssueResult refresh(String accessTokenValue, String refreshTokenValue, ClientInfo clientInfo) {
        RefreshToken refreshToken = jwtTokenProvider.parseRefreshToken(refreshTokenValue);
        AccessToken accessToken = jwtTokenProvider.parseExpiredAccessToken(accessTokenValue);
        LoginSession session = loginSessionReader.getByRefreshJti(refreshToken.jti());

        // 세션이 이미 revoke 상태면 → 탈취 감지, 해당 유저 세션 전체 revoke.
        // 가장 위험한 상황이므로 다른 validation 전에 항상 revoke 여부 먼저 체크할 것
        if (session.isRevoked()) {
            loginSessionWriter.revokeAllByUserId(refreshToken.userId());
            log.warn("[토큰 탈취 감지] session={}, clientInfo={}", session, clientInfo);

            //TODO: 블랙리스트 테이블에 AccessToken jti 등록, 블랙리스트 Polling 로직 구현후 필터에서 차단하도록 구현
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        if (!session.accessJti().equals(accessToken.jti())) {
            log.warn("[토큰 쌍 불일치] session={}, accessToken={}, clientInfo={}",
                    session, accessToken, clientInfo);
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        if (session.clientInfo().isDifferentDevice(clientInfo)) {
            log.warn("[기기 변경 감지] session={}, clientInfo={}", session, clientInfo);
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        // 정상 발급 절차 revoke 처리
        session = session.revoke();
        loginSessionWriter.update(session);

        User user = userReader.findById(refreshToken.userId());

        if (user.status() == UserStatus.BANNED) {
            throw new CoreException(ErrorCode.BANNED);
        }

        return tokenIssueProcessor.issue(user, clientInfo);
    }
}
