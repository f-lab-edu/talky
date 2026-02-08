package org.talky.platform.app.tool;

import org.talky.platform.app.vo.ClientInfo;
import org.talky.platform.app.vo.LoginSession;
import org.talky.platform.storage.entity.LoginSessionEntity;

public class LoginSessionMapper {

    public static LoginSession toVo(LoginSessionEntity entity) {
        ClientInfo clientInfo = ClientInfo.builder()
                .remoteIp(entity.getRemoteIp())
                .uaRawValue(entity.getUaRawValue())
                .uaOsName(entity.getUaOsName())
                .uaDeviceName(entity.getUaDeviceName())
                .uaAgentName(entity.getUaAgentName())
                .uaAgentVersion(entity.getUaAgentVersion())
                .uaDeviceClass(entity.getUaDeviceClass())
                .build();

        return LoginSession.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .accessJti(entity.getAccessJti())
                .refreshJti(entity.getRefreshJti())
                .revokedAt(entity.getRevokedAt())
                .clientInfo(clientInfo)
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static LoginSessionEntity toEntity(LoginSession loginSession) {
        ClientInfo clientInfo = loginSession.clientInfo();

        return LoginSessionEntity.builder()
                .id(loginSession.id())
                .userId(loginSession.userId())
                .accessJti(loginSession.accessJti())
                .refreshJti(loginSession.refreshJti())
                .revokedAt(loginSession.revokedAt())
                .remoteIp(clientInfo.remoteIp())
                .uaRawValue(clientInfo.uaRawValue())
                .uaOsName(clientInfo.uaOsName())
                .uaDeviceName(clientInfo.uaDeviceName())
                .uaAgentName(clientInfo.uaAgentName())
                .uaAgentVersion(clientInfo.uaAgentVersion())
                .uaDeviceClass(clientInfo.uaDeviceClass())
                .expiresAt(loginSession.expiresAt())
                .build();
    }
}
