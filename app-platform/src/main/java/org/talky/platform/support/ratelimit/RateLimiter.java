package org.talky.platform.support.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final RateLimitStorage storage;

    public void check(String ip, RateLimitPolicy policy) {
        String key = policy.endpoint() + ":ip:" + ip;
        int count = storage.incrementAndGet(key, policy.window());

        if (count > policy.limit()) {
            throw new CoreException(ErrorCode.DEFAULT_ERROR);
        }
    }
}