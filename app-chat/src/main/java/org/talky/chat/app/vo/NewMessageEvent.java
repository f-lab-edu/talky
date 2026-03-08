package org.talky.chat.app.vo;

import lombok.Builder;
import org.talky.chat.support.websocket.MessageType;

import java.time.Instant;

@Builder
public record NewMessageEvent(
        MessageType.Out type,
        String messageId,
        String chatId,
        String senderId,
        String content,
        Instant sentAt
) {
    public static NewMessageEvent from(Message message) {
        return NewMessageEvent.builder()
                .type(MessageType.Out.NEW_MESSAGE)
                .messageId(String.valueOf(message.messageId()))
                .chatId(String.valueOf(message.chatId()))
                .senderId(String.valueOf(message.senderId()))
                .content(message.content())
                .sentAt(message.sentAt())
                .build();
    }
}
