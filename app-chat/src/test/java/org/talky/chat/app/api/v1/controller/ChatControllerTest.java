package org.talky.chat.app.api.v1.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.talky.chat.app.api.v1.request.CreateChatRequest;
import org.talky.chat.support.response.ResultType;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
class ChatControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChat {

        @Test
        @DisplayName("1:1 채팅방을 생성하면 채팅방 정보를 반환한다")
        void directChat() {
            CreateChatRequest request = new CreateChatRequest(List.of("김철수#5678"), null);

            webTestClient.post().uri("/api/v1/chats")
                    .header("Authorization", "Bearer test.token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.result").isEqualTo(ResultType.SUCCESS.name())
                    .jsonPath("$.data.channelId").exists()
                    .jsonPath("$.data.type").isEqualTo("direct")
                    .jsonPath("$.data.channelName").exists()
                    .jsonPath("$.data.participants").isArray()
                    .jsonPath("$.error").isEmpty();
        }

        @Test
        @DisplayName("혼자 채팅방을 생성하면 self 타입으로 반환한다")
        void selfChat() {
            CreateChatRequest request = new CreateChatRequest(List.of(), null);

            webTestClient.post().uri("/api/v1/chats")
                    .header("Authorization", "Bearer test.token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.result").isEqualTo(ResultType.SUCCESS.name())
                    .jsonPath("$.data.type").isEqualTo("self")
                    .jsonPath("$.error").isEmpty();
        }

        @Test
        @DisplayName("그룹 채팅방을 생성하면 group 타입으로 반환한다")
        void groupChat() {
            CreateChatRequest request = new CreateChatRequest(
                    List.of("김철수#5678", "박영희#9012"),
                    "스터디 그룹"
            );

            webTestClient.post().uri("/api/v1/chats")
                    .header("Authorization", "Bearer test.token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.result").isEqualTo(ResultType.SUCCESS.name())
                    .jsonPath("$.data.type").isEqualTo("group")
                    .jsonPath("$.error").isEmpty();
        }
    }

    @Nested
    @DisplayName("내 채팅방 목록 조회")
    class GetMyChats {

        @Test
        @DisplayName("채팅방 목록을 조회하면 페이지네이션된 결과를 반환한다")
        void success() {
            webTestClient.get().uri("/api/v1/chats")
                    .header("Authorization", "Bearer test.token")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.result").isEqualTo(ResultType.SUCCESS.name())
                    .jsonPath("$.data.content").isArray()
                    .jsonPath("$.data.hasNext").isBoolean()
                    .jsonPath("$.error").isEmpty();
        }
    }

    @Nested
    @DisplayName("메시지 히스토리 조회")
    class GetMessages {

        @Test
        @DisplayName("메시지 히스토리를 조회하면 페이지네이션된 결과를 반환한다")
        void success() {
            webTestClient.get().uri("/api/v1/chats/{channelId}/messages", "chat_1a2b3c4d")
                    .header("Authorization", "Bearer test.token")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.result").isEqualTo(ResultType.SUCCESS.name())
                    .jsonPath("$.data.content").isArray()
                    .jsonPath("$.data.content[0].channelId").isEqualTo("chat_1a2b3c4d")
                    .jsonPath("$.data.hasNext").isBoolean()
                    .jsonPath("$.error").isEmpty();
        }
    }
}
