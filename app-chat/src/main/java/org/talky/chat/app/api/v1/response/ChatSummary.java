package org.talky.chat.app.api.v1.response;

public record ChatSummary(
        String channelId,
        String type,
        String channelName,
        LastMessageInfo lastMessage,
        int unreadCount
) {
}
