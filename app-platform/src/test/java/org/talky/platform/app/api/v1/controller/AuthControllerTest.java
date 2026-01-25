package org.talky.platform.app.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.talky.platform.app.api.v1.request.LoginRequest;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.support.response.ResultType;
import org.talky.platform.support.error.ErrorCode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("로그인 아이디 중복 체크")
    class CheckLoginId {

        @Test
        @DisplayName("사용 가능한 loginId로 요청하면 사용할 수 있다고 응답한다")
        void available() throws Exception {
            mockMvc.perform(get("/api/v1/auth/check-login-id")
                            .param("loginId", "user1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data.available").value(true))
                    .andExpect(jsonPath("$.error").isEmpty());
        }

        @Test
        @DisplayName("이미 사용 중인 loginId로 요청하면 사용할 수 없다고 응답한다")
        void unavailable() throws Exception {
            mockMvc.perform(get("/api/v1/auth/check-login-id")
                            .param("loginId", "duplicated1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data.available").value(false))
                    .andExpect(jsonPath("$.error").isEmpty());
        }

        @Test
        @DisplayName("4자 미만의 loginId로 요청하면 거부된다")
        void tooShort() throws Exception {
            mockMvc.perform(get("/api/v1/auth/check-login-id")
                            .param("loginId", "abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()));
        }

        @Test
        @DisplayName("20자 초과의 loginId로 요청하면 거부된다")
        void tooLong() throws Exception {
            mockMvc.perform(get("/api/v1/auth/check-login-id")
                            .param("loginId", "abcdefghij12345678901"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()));
        }

        @Test
        @DisplayName("영문+숫자 외 문자가 포함된 loginId로 요청하면 거부된다")
        void invalidChars() throws Exception {
            mockMvc.perform(get("/api/v1/auth/check-login-id")
                            .param("loginId", "user@1234"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()));
        }
    }

    @Nested
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("유효한 요청으로 회원가입하면 사용자 정보를 반환한다")
        void success() throws Exception {
            RegisterRequest request = new RegisterRequest("user1234", "mypassword123", "홍길동");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data.loginId").value("user1234"))
                    .andExpect(jsonPath("$.data.nickname").value("홍길동"))
                    .andExpect(jsonPath("$.data.userTag").exists())
                    .andExpect(jsonPath("$.error").isEmpty());
        }

        @Test
        @DisplayName("4자 미만의 loginId로 요청하면 거부된다")
        void invalidLoginId() throws Exception {
            RegisterRequest request = new RegisterRequest("abc", "mypassword123", "홍길동");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()));
        }

        @Test
        @DisplayName("8자 미만의 password로 요청하면 거부된다")
        void invalidPassword() throws Exception {
            RegisterRequest request = new RegisterRequest("user1234", "pass123", "홍길동");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()));
        }

        @Test
        @DisplayName("2자 미만의 nickname으로 요청하면 거부된다")
        void invalidNickname() throws Exception {
            RegisterRequest request = new RegisterRequest("user1234", "mypassword123", "홍");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.INVALID_INPUT.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_INPUT.getMessage()));
        }

        @Test
        @DisplayName("이미 사용 중인 loginId로 요청하면 거부된다")
        void duplicatedLoginId() throws Exception {
            RegisterRequest request = new RegisterRequest("duplicated1234", "mypassword123", "홍길동");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.DUPLICATED_RESOURCE.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.DUPLICATED_RESOURCE.getMessage()));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("유효한 정보로 로그인하면 사용자 정보와 토큰을 반환한다")
        void success() throws Exception {
            LoginRequest request = new LoginRequest("user1234", "mypassword123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data.loginId").value("user1234"))
                    .andExpect(jsonPath("$.data.nickname").exists())
                    .andExpect(jsonPath("$.data.userTag").exists())
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.error").isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 로그인하면 인증 에러가 발생한다")
        void userNotFound() throws Exception {
            LoginRequest request = new LoginRequest("notexist1234", "mypassword123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.UNAUTHORIZED.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인하면 인증 에러가 발생한다")
        void wrongPassword() throws Exception {
            LoginRequest request = new LoginRequest("user1234", "wrongpassword123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result").value(ResultType.ERROR.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.UNAUTHORIZED.name()))
                    .andExpect(jsonPath("$.error.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("유효한 토큰으로 로그아웃하면 성공한다")
        void success() throws Exception {
            mockMvc.perform(post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.error").isEmpty());
        }
    }
}
