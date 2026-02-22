package org.talky.platform.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talky.platform.storage.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import org.talky.auth.UserStatus;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByUserTag(String userTag);

    Optional<UserEntity> findByLoginIdAndStatusNot(String loginId, UserStatus status);

    Optional<UserEntity> findByIdAndStatusNot(Long id, UserStatus status);

    Optional<UserEntity> findByUserTagAndStatusNot(String userTag, UserStatus status);

    List<UserEntity> findAllByUserTagInAndStatusNot(List<String> userTags, UserStatus status);
}
