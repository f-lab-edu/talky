package org.talky.chat.storage.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.talky.chat.storage.entity.ChatEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatRepository extends ReactiveMongoRepository<ChatEntity, Long> {

    @Query("{ 'participantIds': { $all: ?0, $size: ?1 } }")
    Mono<ChatEntity> findByParticipantIds(List<Long> participantIds, int size);
}
