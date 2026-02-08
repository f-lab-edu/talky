package org.talky.platform.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talky.platform.storage.entity.LoginSessionEntity;

import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSessionEntity, Long> {

    Optional<LoginSessionEntity> findByAccessJti(String accessJti);
}
