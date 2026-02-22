package org.talky.platform.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.TokenIssueResult;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.app.tool.LoginSessionReader;
import org.talky.platform.app.tool.LoginSessionWriter;
import org.talky.platform.app.tool.UserWriter;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.storage.entity.UserEntity;
import org.talky.platform.storage.repository.LoginSessionRepository;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.storage.entity.LoginSessionEntity;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;
import org.talky.auth.AccessToken;
import org.talky.auth.InvalidTokenException;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.RefreshToken;
import org.talky.auth.UserRole;
import org.talky.auth.UserStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
//TODO : 반복적인 테스트 데이터 생성 하는 부분 Fixture 사용 고려
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserWriter userWriter;

    @Autowired
    private LoginSessionRepository loginSessionRepository;

    @Autowired
    private LoginSessionReader loginSessionReader;

    @Autowired
    private LoginSessionWriter loginSessionWriter;

    @Nested
    @DisplayName("로그인")
    class Login {

        private final ClientInfo clientInfo = ClientInfo.builder()
                .remoteIp("127.0.0.1")
                .build();

        @Test
        @DisplayName("정상 로그인 시 토큰 발급 및 세션 저장")
        void success() {
            // given
            registerService.register(new RegisterCommand("logintest", "password123", "닉네임"));

            // when
            TokenIssueResult result = authService.login("logintest", "password123", clientInfo);

            // then
            assertThat(result.user().loginId()).isEqualTo("logintest");
            assertThat(result.accessToken().tokenValue()).isNotNull();
            assertThat(result.refreshToken().tokenValue()).isNotNull();
            assertThat(loginSessionRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 아이디로 로그인 시 예외 발생")
        void userNotFound() {
            assertThatThrownBy(() -> authService.login("notexist", "password123", clientInfo))
                    .isInstanceOf(CoreException.class);
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시 UNAUTHORIZED")
        void wrongPassword() {
            // given
            registerService.register(new RegisterCommand("logintest2", "password123", "닉네임"));

            // when & then
            assertThatThrownBy(() -> authService.login("logintest2", "wrongpassword", clientInfo))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.UNAUTHORIZED);
        }

        @Test
        @DisplayName("정지된 계정으로 로그인 시 BANNED")
        void banned() {
            // given
            registerService.register(new RegisterCommand("banneduser", "password123", "닉네임"));
            UserEntity entity = userRepository.findByLoginIdAndStatusNot("banneduser", UserStatus.DELETED).orElseThrow();
            entity.update(UserEntity.builder()
                    .id(entity.getId())
                    .loginId(entity.getLoginId())
                    .password(entity.getPassword())
                    .nickname(entity.getNickname())
                    .userTag(entity.getUserTag())
                    .role(entity.getRole())
                    .status(UserStatus.BANNED)
                    .build());

            // when & then
            assertThatThrownBy(() -> authService.login("banneduser", "password123", clientInfo))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.BANNED);
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        private final ClientInfo clientInfo = ClientInfo.builder()
                .remoteIp("127.0.0.1")
                .build();

        @Test
        @DisplayName("정상 로그아웃 시 세션이 revoke 된다")
        void success() {
            // given
            registerService.register(new RegisterCommand("logouttest", "password123", "닉네임"));
            TokenIssueResult loginResult = authService.login("logouttest", "password123", clientInfo);

            // when
            authService.logout(loginResult.accessToken().tokenValue());

            // then
            LoginSessionEntity session = loginSessionRepository.findByAccessJti(loginResult.accessToken().jti())
                    .orElseThrow();
            assertThat(session.getRevokedAt()).isNotNull();
        }

        @Test
        @DisplayName("잘못된 토큰으로 로그아웃 시 예외 발생")
        void invalidToken() {
            assertThatThrownBy(() -> authService.logout("invalid.token.value"))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class Refresh {

        private static final String SECRET = "talky-local-dev-secret-key-that-is-at-least-32-bytes";
        private static final long REFRESH_EXPIRY_MS = 1000L * 60 * 60 * 24 * 7;
        private static final JwtTokenProvider SHORT_LIVED_PROVIDER = new JwtTokenProvider(SECRET, 0, REFRESH_EXPIRY_MS);
        private static final JwtTokenProvider NORMAL_PROVIDER = new JwtTokenProvider(SECRET, 1000L * 60 * 30, REFRESH_EXPIRY_MS);

        private final ClientInfo clientInfo = ClientInfo.builder()
                .remoteIp("127.0.0.1")
                .build();

        private Long createUser(String loginId) {
            User user = registerService.register(new RegisterCommand(loginId, "password123", "닉네임"));
            return user.id();
        }

        private void saveSession(Long userId, String accessJti, String refreshJti) {
            loginSessionWriter.save(LoginSession.builder()
                    .id(System.nanoTime())
                    .userId(userId)
                    .accessJti(accessJti)
                    .refreshJti(refreshJti)
                    .clientInfo(clientInfo)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build());
        }

        @Test
        @DisplayName("정상 재발급 시 기존 세션 revoke + 새 토큰 발급 + 새 세션 생성")
        void success() {
            // given
            Long userId = createUser("refreshtest");
            AccessToken expiredAccessToken = SHORT_LIVED_PROVIDER.createAccessToken(userId, UserRole.USER);
            RefreshToken refreshToken = NORMAL_PROVIDER.createRefreshToken(userId);
            saveSession(userId, expiredAccessToken.jti(), refreshToken.jti());

            // when
            TokenIssueResult result = authService.refresh(
                    expiredAccessToken.tokenValue(),
                    refreshToken.tokenValue(),
                    clientInfo
            );

            // then
            assertThat(result.accessToken().tokenValue()).isNotEqualTo(expiredAccessToken.tokenValue());
            assertThat(result.refreshToken().tokenValue()).isNotEqualTo(refreshToken.tokenValue());
            assertThat(result.user().id()).isEqualTo(userId);

            LoginSessionEntity oldSession = loginSessionRepository.findByAccessJti(expiredAccessToken.jti())
                    .orElseThrow();
            assertThat(oldSession.getRevokedAt()).isNotNull();

            assertThat(loginSessionRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("아직 유효한 access token으로 재발급 시도 시 예외")
        void accessTokenStillValid() {
            // given
            Long userId = createUser("refreshtest2");
            AccessToken validAccessToken = NORMAL_PROVIDER.createAccessToken(userId, UserRole.USER);
            RefreshToken refreshToken = NORMAL_PROVIDER.createRefreshToken(userId);
            saveSession(userId, validAccessToken.jti(), refreshToken.jti());

            // when & then
            assertThatThrownBy(() -> authService.refresh(
                    validAccessToken.tokenValue(),
                    refreshToken.tokenValue(),
                    clientInfo
            ))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("이미 revoke된 세션으로 재발급 시도 시 해당 유저 세션 전체 revoke")
        void alreadyRevoked() {
            // given
            Long userId = createUser("refreshtest3");

            AccessToken expiredAccessToken = SHORT_LIVED_PROVIDER.createAccessToken(userId, UserRole.USER);
            RefreshToken refreshToken = NORMAL_PROVIDER.createRefreshToken(userId);
            saveSession(userId, expiredAccessToken.jti(), refreshToken.jti());
            LoginSession session = loginSessionReader.getByAccessJti(expiredAccessToken.jti());
            session = session.revoke();
            loginSessionWriter.update(session);

            AccessToken secondAccessToken = NORMAL_PROVIDER.createAccessToken(userId, UserRole.USER);
            RefreshToken secondRefreshToken = NORMAL_PROVIDER.createRefreshToken(userId);
            saveSession(userId, secondAccessToken.jti(), secondRefreshToken.jti());

            // when & then
            assertThatThrownBy(() -> authService.refresh(
                    expiredAccessToken.tokenValue(),
                    refreshToken.tokenValue(),
                    clientInfo
            ))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.UNAUTHORIZED);

            // 두 번째 세션도 revoke 됨 (전체 revoke)
            LoginSessionEntity secondSession = loginSessionRepository.findByAccessJti(secondAccessToken.jti())
                    .orElseThrow();

            assertThat(secondSession.getRevokedAt()).isNotNull();
        }

        @Test
        @DisplayName("기기 변경 감지 시 예외 발생")
        void differentDevice() {
            // given
            Long userId = createUser("refreshtest4");

            ClientInfo originalClientInfo = ClientInfo.builder()
                    .remoteIp("127.0.0.1")
                    .uaOsName("Windows")
                    .uaDeviceName("Desktop")
                    .uaDeviceClass("Desktop")
                    .build();

            AccessToken expiredAccessToken = SHORT_LIVED_PROVIDER.createAccessToken(userId, UserRole.USER);
            RefreshToken refreshToken = NORMAL_PROVIDER.createRefreshToken(userId);

            loginSessionWriter.save(LoginSession.builder()
                    .id(System.nanoTime())
                    .userId(userId)
                    .accessJti(expiredAccessToken.jti())
                    .refreshJti(refreshToken.jti())
                    .clientInfo(originalClientInfo)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build());

            ClientInfo differentClientInfo = ClientInfo.builder()
                    .remoteIp("192.168.0.1")
                    .uaOsName("Android")
                    .uaDeviceName("Mobile")
                    .uaDeviceClass("Phone")
                    .build();

            // when & then
            assertThatThrownBy(() -> authService.refresh(
                    expiredAccessToken.tokenValue(),
                    refreshToken.tokenValue(),
                    differentClientInfo
            ))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.UNAUTHORIZED);
        }

        @Test
        @DisplayName("정지된 계정으로 토큰 재발급 시 BANNED")
        void banned() {
            // given
            Long userId = createUser("refreshbanned");
            AccessToken expiredAccessToken = SHORT_LIVED_PROVIDER.createAccessToken(userId, UserRole.USER);
            RefreshToken refreshToken = NORMAL_PROVIDER.createRefreshToken(userId);
            saveSession(userId, expiredAccessToken.jti(), refreshToken.jti());

            UserEntity entity = userRepository.findByLoginIdAndStatusNot("refreshbanned", UserStatus.DELETED).orElseThrow();
            entity.update(UserEntity.builder()
                    .loginId(entity.getLoginId())
                    .password(entity.getPassword())
                    .nickname(entity.getNickname())
                    .userTag(entity.getUserTag())
                    .role(entity.getRole())
                    .status(UserStatus.BANNED)
                    .build());

            // when & then
            assertThatThrownBy(() -> authService.refresh(
                    expiredAccessToken.tokenValue(),
                    refreshToken.tokenValue(),
                    clientInfo
            ))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.BANNED);
        }

        @Test
        @DisplayName("잘못된 refresh token으로 재발급 시도 시 예외 발생")
        void invalidRefreshToken() {
            // given
            AccessToken expiredAccessToken = SHORT_LIVED_PROVIDER.createAccessToken(1L, UserRole.USER);

            // when & then
            assertThatThrownBy(() -> authService.refresh(
                    expiredAccessToken.tokenValue(),
                    "invalid.refresh.token",
                    clientInfo
            ))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

}
