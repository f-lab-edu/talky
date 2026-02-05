package org.talky.chat.app.api.v1.controller;

import org.springframework.web.bind.annotation.*;
import org.talky.chat.app.api.v1.request.CreateChatRequest;
import org.talky.chat.app.api.v1.response.*;
import org.talky.chat.support.response.ApiResponse;
import org.talky.chat.support.response.PageResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    @PostMapping
    public Mono<ApiResponse<CreateChatResponse>> createChat(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateChatRequest request
    ) {
        // TODO: 실제 채팅방 생성 로직은 차후 구현
        String type = switch (request.participantTags().size()) {
            case 0 -> "self";
            case 1 -> "direct";
            default -> "group";
        };

        List<ParticipantInfo> participants = List.of(
                new ParticipantInfo("홍길동", "홍길동#1234"),
                new ParticipantInfo("김철수", "김철수#5678")
        );

        CreateChatResponse response = new CreateChatResponse(
                "chat_1a2b3c4d",
                type,
                "홍길동, 김철수",
                participants
        );

        return Mono.just(ApiResponse.success(response));
    }

    @GetMapping
    public Mono<ApiResponse<PageResponse<ChatSummary>>> getMyChats(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        // TODO: 실제 채팅방 목록 조회 로직은 차후 구현
        LastMessageInfo lastMessage = new LastMessageInfo(
                "msg_1a2b3c4d",
                "김철수#5678",
                "김철수",
                "안녕하세요!",
                Instant.parse("2026-01-17T10:30:00Z")
        );

        ChatSummary chatSummary = new ChatSummary(
                "chat_1a2b3c4d",
                "direct",
                "홍길동, 김철수",
                lastMessage,
                3
        );

        PageResponse<ChatSummary> pageResponse = new PageResponse<>(
                List.of(chatSummary),
                false
        );

        return Mono.just(ApiResponse.success(pageResponse));
    }

    @GetMapping("/{channelId}/messages")
    public Mono<ApiResponse<PageResponse<MessageInfo>>> getMessages(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String channelId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        // TODO: 실제 메시지 히스토리 조회 로직은 차후 구현
        MessageInfo message = new MessageInfo(
                "msg_1a2b3c4d",
                channelId,
                "김철수#5678",
                "김철수",
                "안녕하세요!",
                Instant.parse("2026-01-17T10:30:00Z")
        );

        PageResponse<MessageInfo> pageResponse = new PageResponse<>(
                List.of(message),
                false
        );

        return Mono.just(ApiResponse.success(pageResponse));
    }
}
