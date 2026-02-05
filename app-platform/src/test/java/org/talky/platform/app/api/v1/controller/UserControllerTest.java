package org.talky.platform.app.api.v1.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.talky.platform.support.response.ResultType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

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
            given()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test")
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
            given()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test")
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
