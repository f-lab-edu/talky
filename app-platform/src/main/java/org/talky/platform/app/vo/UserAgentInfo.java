package org.talky.platform.app.vo;

import lombok.Builder;

@Builder
public record UserAgentInfo(
        String rawValue,
        String osName,
        String deviceName,
        String agentName,
        String agentVersion,
        String deviceClass
) {
}
