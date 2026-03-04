package org.talky.chat.app.vo;

public record SendMessageCommand(
        Long senderId,
        Long chatId,
        String idempotencyKey,
        String content
) {
}
