package org.talky.platform.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talky.platform.storage.entity.LoginSessionEntity;

public interface LoginSessionRepository extends JpaRepository<LoginSessionEntity, Long> {
}
