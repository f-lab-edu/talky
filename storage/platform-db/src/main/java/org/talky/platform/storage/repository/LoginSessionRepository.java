package org.talky.platform.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.talky.platform.storage.entity.LoginSessionEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSessionEntity, Long> {

    Optional<LoginSessionEntity> findByAccessJti(String accessJti);

    Optional<LoginSessionEntity> findByRefreshJti(String refreshJti);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE LoginSessionEntity s " +
            "SET s.revokedAt = :now " +
            "WHERE s.userId = :userId AND s.revokedAt IS NULL")
    void revokeAllByUserId(Long userId, LocalDateTime now);
}
