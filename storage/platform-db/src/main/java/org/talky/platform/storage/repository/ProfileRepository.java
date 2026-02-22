package org.talky.platform.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talky.platform.storage.entity.ProfileEntity;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByUserId(Long userId);
}
