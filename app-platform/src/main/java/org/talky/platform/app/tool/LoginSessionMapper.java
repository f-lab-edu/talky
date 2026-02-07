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

    public static LoginSessionEntity toEntity(LoginSession vo) {
        ClientInfo clientInfo = vo.clientInfo();

        return LoginSessionEntity.builder()
                .id(vo.id())
                .userId(vo.userId())
                .accessJti(vo.accessJti())
                .refreshJti(vo.refreshJti())
                .remoteIp(clientInfo.remoteIp())
                .uaRawValue(clientInfo.uaRawValue())
                .uaOsName(clientInfo.uaOsName())
                .uaDeviceName(clientInfo.uaDeviceName())
                .uaAgentName(clientInfo.uaAgentName())
                .uaAgentVersion(clientInfo.uaAgentVersion())
                .uaDeviceClass(clientInfo.uaDeviceClass())
                .expiresAt(vo.expiresAt())
                .build();
    }
}
