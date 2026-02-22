package org.talky.chat.app.api.v1.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.talky.auth.JwtTokenProvider;
import org.talky.auth.UserRole;
import org.talky.chat.app.api.v1.request.CreateChatRequest;
import org.talky.chat.storage.repository.ChatRepository;
import org.talky.chat.storage.repository.UserChatRepository;
import org.talky.chat.support.IntegrationTest;
import org.talky.chat.support.response.ResultType;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class ChatControllerTest extends IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserChatRepository userChatRepository;
    private static final Long TEST_USER_ID = 1L;
    private static final JwtTokenProvider JWT = JwtTokenProvider.fromEnv();
    private static final String TEST_TOKEN = "Bearer " + JWT.createAccessToken(TEST_USER_ID, UserRole.USER).tokenValue();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        chatRepository.deleteAll().block();
        userChatRepository.deleteAll().block();
    }

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChat {

        @Test
        @DisplayName("자기 자신과의 채팅방을 생성한다")
        void selfChat() {
            given(platformApiClient.validateCreatingChat(any(), any()))
                    .willReturn(Mono.just(List.of(TEST_USER_ID)));

            given()
                .header("Authorization", TEST_TOKEN)
                .contentType(ContentType.JSON)
                .body(new CreateChatRequest(List.of(), null))
            .when()
                .post("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.chatId", notNullValue())
                .body("data.type", equalTo("SELF"))
                .body("data.participantIds", notNullValue())
                .body("error", nullValue());
        }

        @Test
        @DisplayName("1:1 채팅방을 생성한다")
        void directChat() {
            given(platformApiClient.validateCreatingChat(any(), any()))
                    .willReturn(Mono.just(List.of(TEST_USER_ID, 2L)));

            given()
                .header("Authorization", TEST_TOKEN)
                .contentType(ContentType.JSON)
                .body(new CreateChatRequest(List.of("tag1"), null))
            .when()
                .post("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.chatId", notNullValue())
                .body("data.type", equalTo("DIRECT"))
                .body("data.participantIds", notNullValue())
                .body("error", nullValue());
        }

        @Test
        @DisplayName("그룹 채팅방을 생성한다")
        void groupChat() {
            given(platformApiClient.validateCreatingChat(any(), any()))
                    .willReturn(Mono.just(List.of(TEST_USER_ID, 2L, 3L)));

            given()
                .header("Authorization", TEST_TOKEN)
                .contentType(ContentType.JSON)
                .body(new CreateChatRequest(List.of("tag1", "tag2"), "스터디 그룹"))
            .when()
                .post("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.chatId", notNullValue())
                .body("data.type", equalTo("GROUP"))
                .body("data.participantIds", notNullValue())
                .body("error", nullValue());
        }

    }

    @Nested
    @DisplayName("내 채팅방 목록 조회")
    class GetMyChats {

        @Test
        @DisplayName("채팅방 목록을 조회하면 페이지네이션된 결과를 반환한다")
        void success() {
            given()
                .header("Authorization", TEST_TOKEN)
            .when()
                .get("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.content", notNullValue())
                .body("data.hasNext", notNullValue())
                .body("error", nullValue());
        }
    }
}
