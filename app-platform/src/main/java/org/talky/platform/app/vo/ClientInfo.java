package org.talky.platform.app.vo;

import lombok.Builder;

@Builder
public record ClientInfo(
        String remoteIp,
        String uaRawValue,
        String uaOsName,
        String uaDeviceName,
        String uaAgentName,
        String uaAgentVersion,
        String uaDeviceClass
) {
}
