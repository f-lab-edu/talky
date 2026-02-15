package org.talky.platform.support.auth;

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
import org.talky.platform.support.error.ErrorCode;
import org.talky.platform.support.response.ResultType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorizationFilterTest {

    private static final String SECRET = "talky-local-dev-secret-key-that-is-at-least-32-bytes";
    private static final long REFRESH_EXPIRY_MS = 1000L * 60 * 60 * 24 * 7;
    private static final JwtTokenProvider NORMAL_PROVIDER = new JwtTokenProvider(SECRET, 1000L * 60 * 30, REFRESH_EXPIRY_MS);
    private static final JwtTokenProvider SHORT_LIVED_PROVIDER = new JwtTokenProvider(SECRET, 0, REFRESH_EXPIRY_MS);

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("인증 토큰 없이 요청")
    class NoToken {

        @Test
        @DisplayName("토큰 없이 보호된 API 요청 시 401 UNAUTHORIZED")
        void unauthorized() {
            given()
            .when()
                .get("/api/v1/users/@me")
            .then()
                .statusCode(401)
                .body("result", equalTo(ResultType.ERROR.name()))
                .body("data", nullValue())
                .body("error.code", equalTo(ErrorCode.UNAUTHORIZED.name()));
        }
    }

    @Nested
    @DisplayName("만료된 토큰으로 요청")
    class ExpiredToken {

        @Test
        @DisplayName("만료된 토큰으로 요청 시 401 EXPIRED_TOKEN")
        void expiredToken() {
            AccessToken expiredToken = SHORT_LIVED_PROVIDER.createAccessToken(1L, UserRole.USER);

            given()
                .header("Authorization", "Bearer " + expiredToken.tokenValue())
            .when()
                .get("/api/v1/users/@me")
            .then()
                .statusCode(401)
                .body("result", equalTo(ResultType.ERROR.name()))
                .body("data", nullValue())
                .body("error.code", equalTo(ErrorCode.EXPIRED_TOKEN.name()));
        }
    }

    @Nested
    @DisplayName("유효하지 않은 토큰으로 요청")
    class InvalidToken {

        @Test
        @DisplayName("위변조된 토큰으로 요청 시 401 INVALID_TOKEN")
        void invalidToken() {
            given()
                .header("Authorization", "Bearer invalid.token.value")
            .when()
                .get("/api/v1/users/@me")
            .then()
                .statusCode(401)
                .body("result", equalTo(ResultType.ERROR.name()))
                .body("data", nullValue())
                .body("error.code", equalTo(ErrorCode.INVALID_TOKEN.name()));
        }
    }

    @Nested
    @DisplayName("권한 부족")
    class Forbidden {

        @Test
        @DisplayName("등록되지 않은 경로 요청 시 403 FORBIDDEN (기본값 ADMIN)")
        void forbidden() {
            AccessToken userToken = NORMAL_PROVIDER.createAccessToken(1L, UserRole.USER);

            given()
                .header("Authorization", "Bearer " + userToken.tokenValue())
            .when()
                .get("/api/v1/admin/something")
            .then()
                .statusCode(403)
                .body("result", equalTo(ResultType.ERROR.name()))
                .body("data", nullValue())
                .body("error.code", equalTo(ErrorCode.FORBIDDEN.name()));
        }
    }
}
