package org.talky.platform.app.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.app.api.ClientIpResolver;
import org.talky.platform.support.ratelimit.RateLimitPolicy;
import org.talky.platform.support.ratelimit.RateLimiter;

@Component
@RequiredArgsConstructor
public class ApiFraudChecker {

    private final RateLimiter rateLimiter;

    public void checkLoginId(HttpServletRequest request) {
        String ip = ClientIpResolver.getClientIp(request);
        rateLimiter.check(ip, RateLimitPolicy.CHECK_LOGIN_ID);
    }
}