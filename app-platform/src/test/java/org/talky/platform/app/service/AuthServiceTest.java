package org.talky.platform.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.LoginResult;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.storage.entity.UserEntity;
import org.talky.platform.storage.repository.LoginSessionRepository;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;
import org.talky.auth.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginSessionRepository loginSessionRepository;

    @Nested
    @DisplayName("로그인 아이디 중복 체크")
    class CheckLoginId {

        @Test
        @DisplayName("존재하는 아이디면 true 반환")
        void exists() {
            // given
            userRepository.save(UserEntity.builder()
                    .id(1L)
                    .loginId("existingUser")
                    .password("password")
                    .nickname("nickname")
                    .userTag("tag#1234")
                    .role(UserRole.USER)
                    .build());

            // when
            boolean result = authService.checkLoginId("existingUser");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 아이디면 false 반환")
        void notExists() {
            // given
            // 아무것도 저장하지 않음

            // when
            boolean result = authService.checkLoginId("newUser");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("정상 회원가입")
        void success() {
            // given
            RegisterCommand command = new RegisterCommand("newuser1", "password123", "닉네임");

            // when
            User user = authService.register(command);

            // then
            assertThat(user.loginId()).isEqualTo("newuser1");
            assertThat(user.nickname()).isEqualTo("닉네임");
            assertThat(user.userTag()).isNotNull();
            assertThat(user.password()).isNotEqualTo("password123"); // 암호화됨
        }

        @Test
        @DisplayName("중복 아이디로 가입 시 예외 발생")
        void duplicateLoginId() {
            // given
            userRepository.save(UserEntity.builder()
                    .id(2L)
                    .loginId("existuser")
                    .password("password")
                    .nickname("nickname")
                    .userTag("tag#1234")
                    .role(UserRole.USER)
                    .build());
            RegisterCommand command = new RegisterCommand("existuser", "password123", "닉네임");

            // when & then
            assertThatThrownBy(() -> authService.register(command))
                    .isInstanceOf(CoreException.class);
        }
    }

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
            authService.register(new RegisterCommand("logintest", "password123", "닉네임"));

            // when
            LoginResult result = authService.login("logintest", "password123", clientInfo);

            // then
            assertThat(result.user().loginId()).isEqualTo("logintest");
            assertThat(result.accessToken().tokenValue()).isNotNull();
            assertThat(result.refreshToken().tokenValue()).isNotNull();
            assertThat(loginSessionRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 아이디로 로그인 시 UNAUTHORIZED")
        void userNotFound() {
            assertThatThrownBy(() -> authService.login("notexist", "password123", clientInfo))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.UNAUTHORIZED);
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시 UNAUTHORIZED")
        void wrongPassword() {
            // given
            authService.register(new RegisterCommand("logintest2", "password123", "닉네임"));

            // when & then
            assertThatThrownBy(() -> authService.login("logintest2", "wrongpassword", clientInfo))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.UNAUTHORIZED);
        }
    }
}
