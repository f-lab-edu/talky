package org.talky.chat.app.vo;

import lombok.Builder;
import org.talky.chat.app.vo.SendMessageCommand;

import java.time.Instant;

@Builder
public record Message(
        Long messageId,
        Long chatId,
        Long senderId,
        String content,
        String idempotencyKey,
        Instant sentAt
) {
    public static Message from(Long messageId, SendMessageCommand command) {
        return Message.builder()
                .messageId(messageId)
                .chatId(command.chatId())
                .senderId(command.senderId())
                .content(command.content())
                .idempotencyKey(command.idempotencyKey())
                .build();
    }
}
