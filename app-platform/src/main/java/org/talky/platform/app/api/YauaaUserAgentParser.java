package org.talky.platform.app.api;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;
import org.talky.platform.app.vo.UserAgentInfo;

@Component
public class YauaaUserAgentParser implements UserAgentParser {

    private static final String DEVICE_CLASS = "DeviceClass";
    private static final String DEVICE_NAME = "DeviceName";
    private static final String AGENT_NAME = "AgentName";
    private static final String AGENT_VERSION = "AgentVersion";
    private static final String OS_NAME = "OperatingSystemName";
    private static final int CACHE_SIZE = 1000;

    private final UserAgentAnalyzer analyzer = UserAgentAnalyzer.newBuilder()
            .withFields(DEVICE_CLASS, DEVICE_NAME, AGENT_NAME, AGENT_VERSION, OS_NAME)
            .withCache(CACHE_SIZE)
            .immediateInitialization()
            .build();

    @Override
    public UserAgentInfo parse(String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) {
            return UserAgentInfo.builder().build();
        }
        UserAgent userAgent = analyzer.parse(userAgentString);

        return UserAgentInfo.builder()
                .rawValue(userAgentString)
                .osName(userAgent.getValue(OS_NAME))
                .deviceName(userAgent.getValue(DEVICE_NAME))
                .agentName(userAgent.getValue(AGENT_NAME))
                .agentVersion(userAgent.getValue(AGENT_VERSION))
                .deviceClass(userAgent.getValue(DEVICE_CLASS))
                .build();
    }
}
