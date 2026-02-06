package org.talky.platform.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.talky.platform.app.vo.User;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.storage.entity.UserEntity;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.error.CoreException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("로그인 아이디 중복 체크")
    class CheckLoginId {

        @Test
        @DisplayName("존재하는 아이디면 true 반환")
        void exists() {
            // given
            userRepository.save(new UserEntity("existingUser", "password", "nickname", "tag#1234"));

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
            assertThat(user.getLoginId()).isEqualTo("newuser1");
            assertThat(user.getNickname()).isEqualTo("닉네임");
            assertThat(user.getUserTag()).isNotNull();
            assertThat(user.getPassword()).isNotEqualTo("password123"); // 암호화됨
        }

        @Test
        @DisplayName("중복 아이디로 가입 시 예외 발생")
        void duplicateLoginId() {
            // given
            userRepository.save(new UserEntity("existuser", "password", "nickname", "tag#1234"));
            RegisterCommand command = new RegisterCommand("existuser", "password123", "닉네임");

            // when & then
            assertThatThrownBy(() -> authService.register(command))
                    .isInstanceOf(CoreException.class);
        }
    }
}
