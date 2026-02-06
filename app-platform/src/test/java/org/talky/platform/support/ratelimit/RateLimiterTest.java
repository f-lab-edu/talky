package org.talky.platform.support.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.talky.platform.support.error.CoreException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateLimiterTest {

    private RateLimiter rateLimiter;
    private RateLimitPolicy policy;

    @BeforeEach
    void setUp() {
        RateLimitStorage storage = new InMemoryRateLimitStorage();
        rateLimiter = new RateLimiter(storage);
        policy = new RateLimitPolicy("/test", Duration.ofMinutes(1), 3);
    }

    @Nested
    @DisplayName("Rate Limit 체크")
    class Check {

        @Test
        @DisplayName("limit 이하 요청은 통과")
        void underLimit() {
            // given
            String ip = "192.168.0.1";

            // when & then
            rateLimiter.check(ip, policy);
            rateLimiter.check(ip, policy);
            rateLimiter.check(ip, policy);
            // 3번까지는 예외 없음
        }

        @Test
        @DisplayName("limit 초과 시 예외 발생")
        void overLimit() {
            // given
            String ip = "192.168.0.1";
            rateLimiter.check(ip, policy);
            rateLimiter.check(ip, policy);
            rateLimiter.check(ip, policy);

            // when & then
            assertThatThrownBy(() -> rateLimiter.check(ip, policy))
                    .isInstanceOf(CoreException.class);
        }

        @Test
        @DisplayName("다른 IP는 별도로 카운팅")
        void differentIp() {
            // given
            String ip1 = "192.168.0.1";
            String ip2 = "192.168.0.2";

            // when
            rateLimiter.check(ip1, policy);
            rateLimiter.check(ip1, policy);
            rateLimiter.check(ip1, policy);

            // then - ip2는 아직 limit에 안 걸림
            rateLimiter.check(ip2, policy);
        }

        @Test
        @DisplayName("다른 엔드포인트는 별도로 카운팅")
        void differentEndpoint() {
            // given
            String ip = "192.168.0.1";
            RateLimitPolicy otherPolicy = new RateLimitPolicy("/other", Duration.ofMinutes(1), 3);

            rateLimiter.check(ip, policy);
            rateLimiter.check(ip, policy);
            rateLimiter.check(ip, policy);

            // when & then - 다른 엔드포인트는 별도 카운팅
            rateLimiter.check(ip, otherPolicy);
        }
    }
}
