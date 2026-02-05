package org.talky.chat.app.api.v1.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.talky.chat.app.api.v1.request.CreateChatRequest;
import org.talky.chat.support.response.ResultType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChat {

        @Test
        @DisplayName("1:1 채팅방을 생성하면 채팅방 정보를 반환한다")
        void directChat() {
            CreateChatRequest request = new CreateChatRequest(List.of("김철수#5678"), null);

            given()
                .header("Authorization", "Bearer test.token")
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.channelId", notNullValue())
                .body("data.type", equalTo("direct"))
                .body("data.channelName", notNullValue())
                .body("data.participants", notNullValue())
                .body("error", nullValue());
        }

        @Test
        @DisplayName("혼자 채팅방을 생성하면 self 타입으로 반환한다")
        void selfChat() {
            CreateChatRequest request = new CreateChatRequest(List.of(), null);

            given()
                .header("Authorization", "Bearer test.token")
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.type", equalTo("self"))
                .body("error", nullValue());
        }

        @Test
        @DisplayName("그룹 채팅방을 생성하면 group 타입으로 반환한다")
        void groupChat() {
            CreateChatRequest request = new CreateChatRequest(
                    List.of("김철수#5678", "박영희#9012"),
                    "스터디 그룹"
            );

            given()
                .header("Authorization", "Bearer test.token")
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/chats")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.type", equalTo("group"))
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
                .header("Authorization", "Bearer test.token")
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

    @Nested
    @DisplayName("메시지 히스토리 조회")
    class GetMessages {

        @Test
        @DisplayName("메시지 히스토리를 조회하면 페이지네이션된 결과를 반환한다")
        void success() {
            given()
                .header("Authorization", "Bearer test.token")
            .when()
                .get("/api/v1/chats/{channelId}/messages", "chat_1a2b3c4d")
            .then()
                .statusCode(200)
                .body("result", equalTo(ResultType.SUCCESS.name()))
                .body("data.content", notNullValue())
                .body("data.content[0].channelId", equalTo("chat_1a2b3c4d"))
                .body("data.hasNext", notNullValue())
                .body("error", nullValue());
        }
    }
}
