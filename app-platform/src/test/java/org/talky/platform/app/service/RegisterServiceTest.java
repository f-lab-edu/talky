package org.talky.platform.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.talky.auth.UserRole;
import org.talky.platform.app.vo.RegisterCommand;
import org.talky.platform.app.vo.User;
import org.talky.platform.storage.entity.UserEntity;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.error.CoreException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RegisterServiceTest {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRepository userRepository;

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
            boolean result = registerService.checkLoginId("existingUser");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 아이디면 false 반환")
        void notExists() {
            // given
            // 아무것도 저장하지 않음

            // when
            boolean result = registerService.checkLoginId("newUser");

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
            User user = registerService.register(command);

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
            assertThatThrownBy(() -> registerService.register(command))
                    .isInstanceOf(CoreException.class);
        }
    }
}
