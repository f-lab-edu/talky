package org.talky.chat.storage.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageEntity implements Persistable<Long> {

    @Id
    private Long id;

    private Long chatId;

    private Long senderId;

    private String content;

    @Indexed(unique = true)
    private String idempotencyKey;

    @CreatedDate
    private Instant sentAt;

    @Override
    public boolean isNew() {
        return sentAt == null;
    }

    @Builder
    private MessageEntity(Long id, Long chatId, Long senderId, String content, String idempotencyKey) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.idempotencyKey = idempotencyKey;
    }
}
