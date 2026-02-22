package org.talky.platform.app.api.v1.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.UserRole;
import org.talky.auth.UserStatus;
import org.talky.platform.app.api.v1.request.RegisterRequest;
import org.talky.platform.storage.repository.UserRepository;
import org.talky.platform.support.response.ResultType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private static final String SECRET = "talky-local-dev-secret-key-that-is-at-least-32-bytes";
    private static final long REFRESH_EXPIRY_MS = 1000L * 60 * 60 * 24 * 7;
    private static final JwtTokenProvider NORMAL_PROVIDER = new JwtTokenProvider(SECRET, 1000L * 60 * 30, REFRESH_EXPIRY_MS);

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("내 정보 조회")
    class GetMe {

        @Test
        @DisplayName("유효한 토큰으로 요청하면 내 정보를 반환한다")
        void success() {
            RegisterRequest registerRequest = new RegisterRequest("meuser1", "mypassword123", "내정보유저");
            given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/v1/auth/register");

            Long userId = userRepository.findByLoginIdAndStatusNot("meuser1", UserStatus.DELETED).orElseThrow().getId();
            AccessToken accessToken = NORMAL_PROVIDER.createAccessToken(userId, UserRole.USER);

            given()
                .header("Authorization", "Bearer " + accessToken.tokenValue())
            .when()
                .get("/api/v1/users/@me")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.loginId", equalTo("meuser1"))
                .body("data.nickname", equalTo("내정보유저"))
                .body("data.userTag", notNullValue())
                .body("error", nullValue());
        }
    }

    @Nested
    @DisplayName("사용자 프로필 조회")
    class GetUserProfile {

        @Test
        @DisplayName("존재하는 사용자를 조회하면 프로필 정보를 반환한다")
        void success() {
            RegisterRequest registerRequest = new RegisterRequest("profileuser1", "mypassword123", "프로필유저");
            given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/v1/auth/register");

            String userTag = userRepository.findByLoginIdAndStatusNot("profileuser1", UserStatus.DELETED).orElseThrow().getUserTag();
            AccessToken accessToken = NORMAL_PROVIDER.createAccessToken(1L, UserRole.USER);

            given()
                .header("Authorization", "Bearer " + accessToken.tokenValue())
            .when()
                .get("/api/v1/users/{userTag}/profile", userTag)
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.nickname", equalTo("프로필유저"))
                .body("data.userTag", equalTo(userTag))
                .body("data.profileMessage", equalTo(""))
                .body("error", nullValue());
        }
    }
}
