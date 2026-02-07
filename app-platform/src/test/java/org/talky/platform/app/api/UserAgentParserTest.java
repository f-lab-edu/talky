package org.talky.platform.app.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.talky.platform.app.vo.UserAgentInfo;

import static org.assertj.core.api.Assertions.assertThat;

class UserAgentParserTest {

    private final UserAgentParser parser = new YauaaUserAgentParser();

    @Test
    @DisplayName("정상적인 User-Agent 문자열 파싱")
    void success() {
        // given
        String chromeOnWindows = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

        // when
        UserAgentInfo result = parser.parse(chromeOnWindows);

        // then
        assertThat(result.rawValue()).isEqualTo(chromeOnWindows);
        assertThat(result.osName()).isEqualTo("Windows NT");
        assertThat(result.agentName()).isEqualTo("Chrome");
        assertThat(result.agentVersion()).isNotBlank();
        assertThat(result.deviceName()).isNotBlank();
        assertThat(result.deviceClass()).isEqualTo("Desktop");
    }

    @Test
    @DisplayName("null 입력 시 빈 객체 반환")
    void nullInput() {
        UserAgentInfo result = parser.parse(null);

        assertThat(result.rawValue()).isNull();
        assertThat(result.osName()).isNull();
        assertThat(result.agentName()).isNull();
    }

    @Test
    @DisplayName("빈 문자열 입력 시 빈 객체 반환")
    void blankInput() {
        UserAgentInfo result = parser.parse("  ");

        assertThat(result.rawValue()).isNull();
        assertThat(result.osName()).isNull();
        assertThat(result.agentName()).isNull();
    }
}
