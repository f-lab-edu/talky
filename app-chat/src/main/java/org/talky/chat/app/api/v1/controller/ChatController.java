package org.talky.chat.app.api.v1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.talky.auth.AuthUserId;
import org.talky.chat.app.api.v1.request.CreateChatRequest;
import org.talky.chat.app.api.v1.response.*;
import org.talky.chat.app.service.ChatService;
import org.talky.chat.support.response.ApiResponse;
import org.talky.chat.support.response.PageResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chats")
    public Mono<ApiResponse<CreateChatResponse>> createChat(
            @AuthUserId Long userId,
            @RequestBody CreateChatRequest request
    ) {
        request.validate();
        return chatService.createChat(userId, request.inviteeTags(), request.chatName())
                .map(chat -> ApiResponse.success(CreateChatResponse.from(chat)));
    }

    @GetMapping("/chats")
    public Mono<ApiResponse<PageResponse<ChatSummary>>> getMyChats(
            @AuthUserId Long userId,
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

    @GetMapping("/chats/{chatId}/messages")
    public Mono<ApiResponse<PageResponse<MessageInfo>>> getMessages(
            @AuthUserId Long userId,
            @PathVariable String chatId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        // TODO: 실제 메시지 히스토리 조회 로직은 차후 구현
        MessageInfo message = new MessageInfo(
                "msg_1a2b3c4d",
                chatId,
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
