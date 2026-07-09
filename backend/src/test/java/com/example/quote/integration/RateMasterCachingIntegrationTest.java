package com.example.quote.integration;

import com.example.quote.AbstractIntegrationTest;
import com.example.quote.repository.RateMasterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 次-5対応: 料率マスタ取得（{@link RateMasterRepository}）に{@code @Cacheable}が
 * 正しく適用され、実際にキャッシュへ格納されることをTestcontainers上の実DBで検証する。
 */
@SpringBootTest
class RateMasterCachingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RateMasterRepository rateMasterRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void findByActiveTrueOrderByCategoryAscIdAsc_populatesCacheAfterFirstCall() {
        Cache cache = cacheManager.getCache("activeRateMasters");
        assertThat(cache).isNotNull();
        cache.clear();

        assertThat(cache.get("ALL")).isNull();

        var firstResult = rateMasterRepository.findByActiveTrueOrderByCategoryAscIdAsc();
        assertThat(firstResult).isNotEmpty();

        // 1回目の呼び出し後、キャッシュに結果が格納されていることを確認
        Cache.ValueWrapper cached = cache.get("ALL");
        assertThat(cached).isNotNull();
        assertThat(cached.get()).isEqualTo(firstResult);
    }
}
