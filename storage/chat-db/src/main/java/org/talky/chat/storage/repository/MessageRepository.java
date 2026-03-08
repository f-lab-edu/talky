package org.talky.chat.storage.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.talky.chat.storage.entity.MessageEntity;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveMongoRepository<MessageEntity, Long> {

    Mono<MessageEntity> findByIdempotencyKey(String idempotencyKey);
}
