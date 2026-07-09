package com.example.quote.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 保険料率マスタ（rate_masters）取得のキャッシュ設定。
 *
 * <p>見積作成のたびに毎回全件SELECTしていた料率マスタ取得を、アプリケーションプロセス内の
 * メモリキャッシュ（{@link ConcurrentMapCacheManager}）で保持することでDB負荷を軽減する
 * （次-5対応）。マスタデータは更新頻度が極めて低く、外部キャッシュ（Redis等）を導入するほどの
 * 規模ではないため、追加のインフラを要しないインプロセス実装を採用している。
 *
 * <p><b>キャッシュ無効化について</b>: 現時点では料率マスタを変更する管理API・管理画面は
 * 実装されていないため、キャッシュの自動失効機構は設けていない（アプリケーション再起動でのみ
 * クリアされる）。将来、料率マスタの変更APIを実装する場合は、その更新処理に
 * {@code @CacheEvict(value = "activeRateMasters", allEntries = true)} を付与するか、
 * {@code cacheManager.getCache("activeRateMasters").clear()} を呼び出すことでキャッシュを
 * 破棄すること。
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("activeRateMasters");
    }
}
