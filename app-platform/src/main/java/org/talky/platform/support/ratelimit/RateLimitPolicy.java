package org.talky.platform.support.ratelimit;

import java.time.Duration;

/**
 * Fixed Window 방식의 Rate Limit 정책
 *
 * @param endpoint Rate Limit을 적용할 API 엔드포인트 (저장소 키로 사용)
 * @param window   제한 윈도우 기간 (예: 1일, 1시간)
 * @param limit    윈도우 기간 내 최대 허용 요청 수
 */
public record RateLimitPolicy(
        String endpoint,
        Duration window,
        int limit
) {
    public static final RateLimitPolicy CHECK_LOGIN_ID =
            new RateLimitPolicy("/api/v1/auth/check-login-id", Duration.ofDays(1), 100);
}