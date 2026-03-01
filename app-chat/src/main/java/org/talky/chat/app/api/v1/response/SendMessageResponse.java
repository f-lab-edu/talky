package org.talky.chat.app.api.v1.response;

import org.talky.chat.app.vo.Message;

import java.time.Instant;

public record SendMessageResponse(
        String messageId,
        String chatId,
        String senderId,
        String content,
        String idempotencyKey,
        Instant sentAt
) {
    public static SendMessageResponse from(Message message) {
        return new SendMessageResponse(
                String.valueOf(message.messageId()),
                String.valueOf(message.chatId()),
                String.valueOf(message.senderId()),
                message.content(),
                message.idempotencyKey(),
                message.sentAt()
        );
    }
}
