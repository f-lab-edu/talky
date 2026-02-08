package org.talky.platform.app.api.v1.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.talky.platform.app.api.v1.request.LoginRequest;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.support.response.ResultType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("로그인 아이디 중복 체크")
    class CheckLoginId {

        @Test
        @DisplayName("존재하지 않는 loginId면 exists=false를 응답한다")
        void notExists() {
            given()
                .param("loginId", "user12341234")
            .when()
                .get("/api/v1/auth/check-login-id")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.exists", equalTo(false))
                .body("error", nullValue());
        }

//        @Test
//        @DisplayName("존재하는 loginId면 exists=true를 응답한다")
//        void exists() {
//            given()
//                .param("loginId", "duplicated1234")
//            .when()
//                .get("/api/v1/auth/check-login-id")
//            .then()
//                .statusCode(200)
//                .body("result", equalTo(ResultType.SUCCESS.name()))
//                .body("data.exists", equalTo(true))
//                .body("error", nullValue());
//        }
//
//        @Test
//        @DisplayName("4자 미만의 loginId로 요청하면 거부된다")
//        void tooShort() {
//            given()
//                .param("loginId", "abc")
//            .when()
//                .get("/api/v1/auth/check-login-id")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.INVALID_INPUT.name()))
//                .body("error.message", equalTo(ErrorCode.INVALID_INPUT.getMessage()));
//        }
//
//        @Test
//        @DisplayName("20자 초과의 loginId로 요청하면 거부된다")
//        void tooLong() {
//            given()
//                .param("loginId", "abcdefghij12345678901")
//            .when()
//                .get("/api/v1/auth/check-login-id")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.INVALID_INPUT.name()))
//                .body("error.message", equalTo(ErrorCode.INVALID_INPUT.getMessage()));
//        }
//
//        @Test
//        @DisplayName("영문+숫자 외 문자가 포함된 loginId로 요청하면 거부된다")
//        void invalidChars() {
//            given()
//                .param("loginId", "user@1234")
//            .when()
//                .get("/api/v1/auth/check-login-id")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.INVALID_INPUT.name()))
//                .body("error.message", equalTo(ErrorCode.INVALID_INPUT.getMessage()));
//        }
    }

    @Nested
    @DisplayName("회원가입")
    class Register {

        @Test
        @DisplayName("유효한 요청으로 회원가입하면 사용자 정보를 반환한다")
        void success() {
            RegisterRequest request = new RegisterRequest("user1234", "mypassword123", "홍길동");

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/auth/register")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.loginId", equalTo("user1234"))
                .body("data.nickname", equalTo("홍길동"))
                .body("data.userTag", notNullValue())
                .body("error", nullValue());
        }
//
//        @Test
//        @DisplayName("4자 미만의 loginId로 요청하면 거부된다")
//        void invalidLoginId() {
//            RegisterRequest request = new RegisterRequest("abc", "mypassword123", "홍길동");
//
//            given()
//                .contentType(ContentType.JSON)
//                .body(request)
//            .when()
//                .post("/api/v1/auth/register")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.INVALID_INPUT.name()))
//                .body("error.message", equalTo(ErrorCode.INVALID_INPUT.getMessage()));
//        }
//
//        @Test
//        @DisplayName("8자 미만의 password로 요청하면 거부된다")
//        void invalidPassword() {
//            RegisterRequest request = new RegisterRequest("user1234", "pass123", "홍길동");
//
//            given()
//                .contentType(ContentType.JSON)
//                .body(request)
//            .when()
//                .post("/api/v1/auth/register")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.INVALID_INPUT.name()))
//                .body("error.message", equalTo(ErrorCode.INVALID_INPUT.getMessage()));
//        }
//
//        @Test
//        @DisplayName("2자 미만의 nickname으로 요청하면 거부된다")
//        void invalidNickname() {
//            RegisterRequest request = new RegisterRequest("user1234", "mypassword123", "홍");
//
//            given()
//                .contentType(ContentType.JSON)
//                .body(request)
//            .when()
//                .post("/api/v1/auth/register")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.INVALID_INPUT.name()))
//                .body("error.message", equalTo(ErrorCode.INVALID_INPUT.getMessage()));
//        }
//
//        @Test
//        @DisplayName("이미 사용 중인 loginId로 요청하면 거부된다")
//        void duplicatedLoginId() {
//            RegisterRequest request = new RegisterRequest("duplicated1234", "mypassword123", "홍길동");
//
//            given()
//                .contentType(ContentType.JSON)
//                .body(request)
//            .when()
//                .post("/api/v1/auth/register")
//            .then()
//                .statusCode(400)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.DUPLICATED_RESOURCE.name()))
//                .body("error.message", equalTo(ErrorCode.DUPLICATED_RESOURCE.getMessage()));
//        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("유효한 정보로 로그인하면 사용자 정보와 토큰을 반환한다")
        void success() {
            RegisterRequest registerRequest = new RegisterRequest("loginuser1", "mypassword123", "테스트");
            given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/v1/auth/register");

            LoginRequest request = new LoginRequest("loginuser1", "mypassword123");

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/auth/login")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.loginId", equalTo("loginuser1"))
                .body("data.nickname", notNullValue())
                .body("data.userTag", notNullValue())
                .body("data.accessToken", notNullValue())
                .body("data.refreshToken", notNullValue())
                .body("error", nullValue());
        }

//        @Test
//        @DisplayName("존재하지 않는 사용자로 로그인하면 인증 에러가 발생한다")
//        void userNotFound() {
//            LoginRequest request = new LoginRequest("notexist1234", "mypassword123");
//
//            given()
//                .contentType(ContentType.JSON)
//                .body(request)
//            .when()
//                .post("/api/v1/auth/login")
//            .then()
//                .statusCode(401)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.UNAUTHORIZED.name()))
//                .body("error.message", equalTo(ErrorCode.UNAUTHORIZED.getMessage()));
//        }
//
//        @Test
//        @DisplayName("잘못된 비밀번호로 로그인하면 인증 에러가 발생한다")
//        void wrongPassword() {
//            LoginRequest request = new LoginRequest("user1234", "wrongpassword123");
//
//            given()
//                .contentType(ContentType.JSON)
//                .body(request)
//            .when()
//                .post("/api/v1/auth/login")
//            .then()
//                .statusCode(401)
//                .body("result", equalTo(ResultType.ERROR.name()))
//                .body("data", nullValue())
//                .body("error.code", equalTo(ErrorCode.UNAUTHORIZED.name()))
//                .body("error.message", equalTo(ErrorCode.UNAUTHORIZED.getMessage()));
//        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("유효한 토큰으로 로그아웃하면 성공한다")
        void success() {
            RegisterRequest registerRequest = new RegisterRequest("logoutuser1", "mypassword123", "테스트");
            given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/v1/auth/register");

            LoginRequest loginRequest = new LoginRequest("logoutuser1", "mypassword123");
            String accessToken = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
            .when()
                .post("/api/v1/auth/login")
            .then()
                .extract()
                .path("data.accessToken");

            given()
                .header("Authorization", "Bearer " + accessToken)
            .when()
                .post("/api/v1/auth/logout")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data", nullValue())
                .body("error", nullValue());
        }
    }
}
