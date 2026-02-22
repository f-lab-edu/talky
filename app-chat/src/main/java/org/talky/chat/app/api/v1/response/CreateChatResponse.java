package org.talky.chat.app.api.v1.response;

import org.talky.chat.app.vo.Chat;

import java.util.List;

public record CreateChatResponse(
        String chatId,
        String creatorId,
        String type,
        String chatName,
        List<String> participantIds
) {
    public static CreateChatResponse from(Chat chat) {
        return new CreateChatResponse(
                String.valueOf(chat.chatId()),
                String.valueOf(chat.creatorId()),
                chat.type().name(),
                chat.chatName(),
                chat.participantIds().stream()
                        .map(String::valueOf)
                        .toList()
        );
    }
}
