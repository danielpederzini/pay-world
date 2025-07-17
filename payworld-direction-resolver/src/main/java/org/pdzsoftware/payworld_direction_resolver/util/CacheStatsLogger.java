package org.pdzsoftware.payworld_direction_resolver.util;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class CacheStatsLogger {
    public static final String CACHE_NAME = "conversionRates";
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 */10 * * * *")// Every 10 minutes
    private void logCacheStats() {
        Cache springCache = cacheManager.getCache(CACHE_NAME);

        if (springCache instanceof CaffeineCache caffeineCache) {
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            CacheStats stats = nativeCache.stats();

            log.info("Cache '{}' stats: hits={}, misses={}, loadSuccess={}, loadFailure={}, totalLoadTime={}ns, evictions={}",
                    CACHE_NAME,
                    stats.hitCount(),
                    stats.missCount(),
                    stats.loadSuccessCount(),
                    stats.loadFailureCount(),
                    stats.totalLoadTime(),
                    stats.evictionCount()
            );
        } else {
            log.warn("Cache '{}' is not a CaffeineCache (was {}).", CACHE_NAME, springCache);
        }
    }
}
