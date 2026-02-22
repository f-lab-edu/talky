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
import org.talky.chat.storage.enums.EChatType;

import java.time.Instant;
import java.util.List;

@Document(collection = "chats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatEntity implements Persistable<Long> {

    @Id
    private Long id;

    private Long creatorId;

    private EChatType type;

    private String chatName;

    private List<Long> participantIds;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    @Builder
    private ChatEntity(Long id, Long creatorId, EChatType type, String chatName, List<Long> participantIds) {
        this.id = id;
        this.creatorId = creatorId;
        this.type = type;
        this.chatName = chatName;
        this.participantIds = participantIds;
    }
}
