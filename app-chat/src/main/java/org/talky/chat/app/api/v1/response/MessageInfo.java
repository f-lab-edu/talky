package org.talky.chat.app.api.v1.response;

import java.time.Instant;

public record MessageInfo(
        String messageId,
        String chatId,
        String senderTag,
        String senderNickname,
        String content,
        Instant timestamp
) {
}
