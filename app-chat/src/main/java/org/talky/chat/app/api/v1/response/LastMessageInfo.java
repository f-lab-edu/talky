package org.talky.chat.app.api.v1.response;

import java.time.Instant;

public record LastMessageInfo(
        String messageId,
        String senderTag,
        String senderNickname,
        String content,
        Instant timestamp
) {
}
