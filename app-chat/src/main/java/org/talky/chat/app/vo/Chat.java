package org.talky.chat.app.vo;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record Chat(
        Long chatId,
        Long creatorId,
        ChatType type,
        String chatName,
        List<Long> participantIds,
        Instant createdAt,
        Instant updatedAt
) {
}
