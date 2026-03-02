package org.talky.chat.app.api.v1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.talky.auth.AuthUserId;
import org.talky.chat.app.api.v1.request.SendMessageRequest;
import org.talky.chat.app.api.v1.response.SendMessageResponse;
import org.talky.chat.app.service.MessageService;
import org.talky.chat.support.response.ApiResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/chats/{chatId}/messages")
    public Mono<ApiResponse<SendMessageResponse>> sendMessage(
            @AuthUserId Long userId,
            @PathVariable String chatId,
            @RequestBody SendMessageRequest request
    ) {
        request.validate();
        return messageService.sendMessage(request.toCommand(userId, Long.parseLong(chatId)))
                .map(message -> ApiResponse.success(SendMessageResponse.from(message)));
    }
}
