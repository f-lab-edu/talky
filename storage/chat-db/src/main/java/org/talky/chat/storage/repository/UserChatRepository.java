package org.talky.chat.storage.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.talky.chat.storage.entity.UserChatEntity;

public interface UserChatRepository extends ReactiveMongoRepository<UserChatEntity, Long> {
}
