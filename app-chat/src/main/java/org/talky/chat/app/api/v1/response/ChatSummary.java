package org.talky.chat.app.api.v1.response;

public record ChatSummary(
        String chatId,
        String type,
        String chatName,
        LastMessageInfo lastMessage,
        int unreadCount
) {
}
