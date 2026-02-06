package org.talky.platform.app.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ClientIpResolverTest {

    @Test
    @DisplayName("X-Forwarded-For 헤더가 있으면 첫 번째 IP 추출")
    void useForwardedHeader() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18");

        // when
        String clientIp = ClientIpResolver.getClientIp(request);

        // then
        assertThat(clientIp).isEqualTo("203.0.113.195");
    }

    @Test
    @DisplayName("프록시 헤더가 없으면 기본 RemoteAddr 반환")
    void useRemoteAddr() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.0.1");

        // when
        String clientIp = ClientIpResolver.getClientIp(request);

        // then
        assertThat(clientIp).isEqualTo("192.168.0.1");
    }
}