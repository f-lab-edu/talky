package org.talky.platform.app.api.v1.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talky.platform.app.api.v1.request.CreateChatCheckRequest;
import org.talky.platform.app.api.v1.response.CreateChatCheckResponse;
import org.talky.platform.app.service.ChatService;
import org.talky.platform.app.service.UserService;
import org.talky.platform.app.vo.User;
import org.talky.platform.support.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @PostMapping("/internal/chats/create-check")
    public ApiResponse<CreateChatCheckResponse> createChatCheck(
            @RequestBody CreateChatCheckRequest request
    ) {
        chatService.validateChatCreation(request.creatorId(), request.inviteeTags());
        List<User> invitedUsers = userService.findByUserTags(request.inviteeTags());
        return ApiResponse.success(CreateChatCheckResponse.from(request.creatorId(), invitedUsers));
    }
}
