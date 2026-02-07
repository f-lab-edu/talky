package org.talky.platform.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.RefreshToken;
import org.talky.auth.UserRole;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserReader userReader;
    private final UserWriter userWriter;
    private final UserTagGenerator userTagGenerator;
    private final RegisterValidator registerValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserIdGenerator userIdGenerator;
    private final JwtTokenProvider jwtTokenProvider;
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
        User user = userReader.findByLoginId(loginId)
                .orElseThrow(() -> new CoreException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(password, user.password())) {
            throw new CoreException(ErrorCode.UNAUTHORIZED);
        }

        AccessToken accessToken = jwtTokenProvider.createAccessToken(user.id(), user.role());
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken(user.id());

        LoginSession loginSession = LoginSession.builder()
                .id(tsidGenerator.generate())
                .userId(user.id())
                .accessJti(accessToken.jti())
                .refreshJti(refreshToken.jti())
                .clientInfo(clientInfo)
                .expiresAt(refreshToken.expiresAt())
                .build();

        loginSessionWriter.save(loginSession);
        return new LoginResult(user, accessToken, refreshToken);
    }
}
