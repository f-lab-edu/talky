package org.talky.chat.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talky.chat.app.vo.ChatType;
import org.talky.chat.storage.repository.ChatRepository;
import org.talky.chat.storage.repository.UserChatRepository;
import org.talky.chat.support.IntegrationTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class ChatServiceTest extends IntegrationTest {

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserChatRepository userChatRepository;

    private static final Long CREATOR_ID = 1L;
    private static final Long INVITEE_ID = 2L;

    @BeforeEach
    void setUp() {
        chatRepository.deleteAll().block();
        userChatRepository.deleteAll().block();
    }

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChat {

        @Test
        @DisplayName("GROUP 타입은 항상 새로운 채팅방을 생성한다")
        void group() {
            List<Long> participantIds = List.of(CREATOR_ID, INVITEE_ID, 3L);
            given(platformApiClient.validateCreatingChat(CREATOR_ID, List.of("tag1", "tag2")))
                    .willReturn(Mono.just(participantIds));

            chatService.createChat(CREATOR_ID, List.of("tag1", "tag2"), null).block();
            chatService.createChat(CREATOR_ID, List.of("tag1", "tag2"), null).block();

            assertThat(chatRepository.count().block()).isEqualTo(2);
        }

        @Test
        @DisplayName("SELF 타입은 처음 생성 시 새로운 채팅방을 만든다")
        void selfNew() {
            List<Long> participantIds = List.of(CREATOR_ID);
            given(platformApiClient.validateCreatingChat(CREATOR_ID, List.of()))
                    .willReturn(Mono.just(participantIds));

            StepVerifier.create(chatService.createChat(CREATOR_ID, List.of(), null))
                    .assertNext(chat -> {
                        assertThat(chat.type()).isEqualTo(ChatType.SELF);
                        assertThat(chat.participantIds()).containsExactly(CREATOR_ID);
                    })
                    .verifyComplete();

            assertThat(chatRepository.count().block()).isEqualTo(1);
        }

        @Test
        @DisplayName("SELF 타입은 이미 채팅방이 있으면 기존 채팅방을 반환한다")
        void selfExisting() {
            List<Long> participantIds = List.of(CREATOR_ID);
            given(platformApiClient.validateCreatingChat(CREATOR_ID, List.of()))
                    .willReturn(Mono.just(participantIds));

            Long existingChatId = chatService.createChat(CREATOR_ID, List.of(), null).block().chatId();

            StepVerifier.create(chatService.createChat(CREATOR_ID, List.of(), null))
                    .assertNext(chat -> assertThat(chat.chatId()).isEqualTo(existingChatId))
                    .verifyComplete();

            assertThat(chatRepository.count().block()).isEqualTo(1);
        }

        @Test
        @DisplayName("DIRECT 타입은 처음 생성 시 새로운 채팅방을 만든다")
        void directNew() {
            List<Long> participantIds = List.of(CREATOR_ID, INVITEE_ID);
            given(platformApiClient.validateCreatingChat(CREATOR_ID, List.of("tag1")))
                    .willReturn(Mono.just(participantIds));

            StepVerifier.create(chatService.createChat(CREATOR_ID, List.of("tag1"), null))
                    .assertNext(chat -> {
                        assertThat(chat.type()).isEqualTo(ChatType.DIRECT);
                        assertThat(chat.participantIds()).containsExactlyInAnyOrder(CREATOR_ID, INVITEE_ID);
                    })
                    .verifyComplete();

            assertThat(chatRepository.count().block()).isEqualTo(1);
        }

        @Test
        @DisplayName("DIRECT 타입은 이미 채팅방이 있으면 기존 채팅방을 반환한다")
        void directExisting() {
            List<Long> participantIds = List.of(CREATOR_ID, INVITEE_ID);
            given(platformApiClient.validateCreatingChat(CREATOR_ID, List.of("tag1")))
                    .willReturn(Mono.just(participantIds));

            Long existingChatId = chatService.createChat(CREATOR_ID, List.of("tag1"), null).block().chatId();

            StepVerifier.create(chatService.createChat(CREATOR_ID, List.of("tag1"), null))
                    .assertNext(chat -> assertThat(chat.chatId()).isEqualTo(existingChatId))
                    .verifyComplete();

            assertThat(chatRepository.count().block()).isEqualTo(1);
        }
    }
}
