package org.talky.platform.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talky.platform.storage.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByUserTag(String userTag);
}
