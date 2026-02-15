package org.talky.platform.app.api.v1.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.talky.auth.AccessToken;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.UserRole;
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
            AccessToken accessToken = NORMAL_PROVIDER.createAccessToken(1L, UserRole.USER);

            given()
                .header("Authorization", "Bearer " + accessToken.tokenValue())
            .when()
                .get("/api/v1/users/@me")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.loginId", notNullValue())
                .body("data.nickname", notNullValue())
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
            AccessToken accessToken = NORMAL_PROVIDER.createAccessToken(1L, UserRole.USER);

            given()
                .header("Authorization", "Bearer " + accessToken.tokenValue())
            .when()
                .get("/api/v1/users/{userTag}", "김철수#5678")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.nickname", notNullValue())
                .body("data.userTag", notNullValue())
                .body("data.profileMessage", notNullValue())
                .body("data.loginId", nullValue())
                .body("error", nullValue());
        }
    }
}
