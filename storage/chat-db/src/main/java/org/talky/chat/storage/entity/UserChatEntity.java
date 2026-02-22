package org.talky.chat.storage.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user_chats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChatEntity implements Persistable<Long> {

    @Id
    private Long id;

    private Long userId;

    private Long chatId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    @Builder
    private UserChatEntity(Long id, Long userId, Long chatId) {
        this.id = id;
        this.userId = userId;
        this.chatId = chatId;
    }
}
