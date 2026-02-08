package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.RefreshToken;
import org.talky.auth.UserRole;
import org.talky.platform.app.tool.LoginSessionReader;
import org.talky.platform.app.tool.LoginSessionWriter;
import org.talky.platform.app.tool.TsidGenerator;
import org.talky.platform.app.tool.UserIdGenerator;
import org.talky.platform.app.tool.RegisterValidator;
import org.talky.platform.app.tool.UserReader;
import org.talky.platform.app.tool.UserTagGenerator;
import org.talky.platform.app.tool.UserWriter;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.LoginResult;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;
@Slf4j
@Service
@RequiredArgsConstructor
//TODO: 별도 리팩토링 필요, (의존필드 줄이기)
public class AuthService {

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final UserTagGenerator userTagGenerator;
    private final RegisterValidator registerValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserIdGenerator userIdGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSessionReader loginSessionReader;
    private final LoginSessionWriter loginSessionWriter;
    private final TsidGenerator tsidGenerator;

    @Transactional(readOnly = true)
    public boolean checkLoginId(String loginId) {
        return userReader.existLoginId(loginId);
    }

    @Transactional
    public User register(RegisterCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        String userTag = userTagGenerator.generate();
        Long userId = userIdGenerator.generate();
        User user = User.builder()
                .id(userId)
                .loginId(command.loginId())
                .password(encodedPassword)
                .nickname(command.nickname())
                .userTag(userTag)
                .role(UserRole.USER)
                .build();

        registerValidator.validate(user);
        return userWriter.save(user);
    }

    @Transactional
    public LoginResult login(String loginId, String password, ClientInfo clientInfo) {
        User user = userReader.findByLoginId(loginId);

        if (!passwordEncoder.matches(password, user.password())) {
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        AccessToken accessToken = jwtTokenProvider.createAccessToken(user.id(), user.role());
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(user.id());

        LoginSession loginSession = LoginSession.of(tsidGenerator.generate(), user.id(), accessToken, refreshToken, clientInfo);

        loginSessionWriter.save(loginSession);
        return new LoginResult(user, accessToken, refreshToken);
    }

    @Transactional
    public void logout(String accessToken) {
        AccessToken token = jwtTokenProvider.parseAccessToken(accessToken);
        loginSessionWriter.revoke(token.jti());
    }

    //TODO : LoginResult 이름 변경 고려 -> AuthResult
    @Transactional
    public LoginResult refresh(String accessTokenValue, String refreshTokenValue, ClientInfo clientInfo) {
        RefreshToken refreshToken = jwtTokenProvider.parseRefreshToken(refreshTokenValue);
        AccessToken accessToken = jwtTokenProvider.parseExpiredAccessToken(accessTokenValue);
        LoginSession session = loginSessionReader.getByRefreshJti(refreshToken.jti());

        if (!session.accessJti().equals(accessToken.jti())) {
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        // 세션이 이미 revoke 상태면 → 탈취 감지, 해당 유저 세션 전체 revoke
        if (session.isRevoked()) {
            loginSessionWriter.revokeAllByUserId(refreshToken.userId());
            log.warn("[토큰 탈취 감지] userId={}, refreshJti={}, accessJti={}, ip={}",
                    refreshToken.userId(), refreshToken.jti(), accessToken.jti(), clientInfo.remoteIp());

            //TODO: 블랙리스트 테이블에 AccessToken jti 등록, 블랙리스트 Polling 로직 구현후 필터에서 차단하도록 구현
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        if (session.clientInfo().isDifferentDevice(clientInfo)) {
            log.warn("[기기 변경 감지] userId={}, original={}, request={}",
                    refreshToken.userId(), session.clientInfo(), clientInfo);
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        // 정상 발급 절차 revoke 처리
        loginSessionWriter.revoke(session.accessJti());

        User user = userReader.findById(refreshToken.userId());

        AccessToken newAccessToken = jwtTokenProvider.createAccessToken(user.id(), user.role());
        RefreshToken newRefreshToken = jwtTokenProvider.createRefreshToken(user.id());

        LoginSession newSession = LoginSession.of(tsidGenerator.generate(), user.id(), newAccessToken, newRefreshToken, clientInfo);

        loginSessionWriter.save(newSession);
        return new LoginResult(user, newAccessToken, newRefreshToken);
    }
}
