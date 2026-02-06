package org.talky.platform.support.ratelimit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRateLimitStorage implements RateLimitStorage {

    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 1000 * 60 * 60) // 1시간마다
    public void cleanup() {
        Instant now = Instant.now();
        counters.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    @Override
    public int incrementAndGet(String key, Duration ttl) {
        Instant now = Instant.now();

        Counter counter = counters.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired(now)) {
                return new Counter(1, now.plus(ttl));
            }
            return existing.increment();
        });

        return counter.count();
    }

    private record Counter(int count, Instant expiresAt) {
        boolean isExpired(Instant now) {
            return now.isAfter(expiresAt);
        }

        Counter increment() {
            return new Counter(count + 1, expiresAt);
        }
    }
}
