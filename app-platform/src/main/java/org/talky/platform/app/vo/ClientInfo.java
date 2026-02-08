package org.talky.platform.app.vo;

import lombok.Builder;

import java.util.Objects;

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
    public static ClientInfo of(String remoteIp, UserAgentInfo userAgentInfo) {
        return ClientInfo.builder()
                .remoteIp(remoteIp)
                .uaRawValue(userAgentInfo.rawValue())
                .uaOsName(userAgentInfo.osName())
                .uaDeviceName(userAgentInfo.deviceName())
                .uaAgentName(userAgentInfo.agentName())
                .uaAgentVersion(userAgentInfo.agentVersion())
                .uaDeviceClass(userAgentInfo.deviceClass())
                .build();
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "ip=" + remoteIp +
                ", rawUA=" + uaRawValue +
                ", os=" + uaOsName +
                ", device=" + uaDeviceName +
                ", agent=" + uaAgentName +
                ", version=" + uaAgentVersion +
                ", class=" + uaDeviceClass +
                '}';
    }

    public boolean isDifferentDevice(ClientInfo other) {
        return !Objects.equals(uaOsName, other.uaOsName)
                || !Objects.equals(uaDeviceName, other.uaDeviceName)
                || !Objects.equals(uaDeviceClass, other.uaDeviceClass);
    }
}
