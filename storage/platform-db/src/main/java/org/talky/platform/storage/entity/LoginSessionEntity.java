package org.talky.platform.storage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginSessionEntity {

    @Id
    private Long id;

    private Long userId;

    private String accessJti;

    private String refreshJti;

    private LocalDateTime revokedAt;

    private String remoteIp;

    private String uaRawValue;

    private String uaOsName;

    private String uaDeviceName;

    private String uaAgentName;

    private String uaAgentVersion;

    private String uaDeviceClass;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    @Builder
    private LoginSessionEntity(Long id, Long userId, String accessJti, String refreshJti,
                               LocalDateTime revokedAt,
                               String remoteIp, String uaRawValue,
                               String uaOsName, String uaDeviceName, String uaAgentName,
                               String uaAgentVersion, String uaDeviceClass,
                               LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.accessJti = accessJti;
        this.refreshJti = refreshJti;
        this.revokedAt = revokedAt;
        this.remoteIp = remoteIp;
        this.uaRawValue = uaRawValue;
        this.uaOsName = uaOsName;
        this.uaDeviceName = uaDeviceName;
        this.uaAgentName = uaAgentName;
        this.uaAgentVersion = uaAgentVersion;
        this.uaDeviceClass = uaDeviceClass;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }
}
