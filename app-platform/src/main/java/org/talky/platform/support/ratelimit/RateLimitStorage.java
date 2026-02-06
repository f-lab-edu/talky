package org.talky.platform.support.ratelimit;

import java.time.Duration;

public interface RateLimitStorage {

    int incrementAndGet(String key, Duration ttl);
}
