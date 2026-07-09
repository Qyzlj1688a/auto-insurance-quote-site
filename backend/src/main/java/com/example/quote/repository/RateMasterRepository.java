package com.example.quote.repository;

import com.example.quote.entity.RateMaster;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 保険料率マスタ用リポジトリインターフェース。
 *
 * <p>見積作成・料率一覧取得のたびに毎回全件SELECTするのを避けるため、有効な料率取得系メソッドには
 * {@link Cacheable}（{@code activeRateMasters}キャッシュ、{@link com.example.quote.config.CacheConfig}参照）
 * を付与している。Spring DataリポジトリはSpringが管理する通常のBeanであるため、
 * クエリ実行のためのAOPアドバイスとキャッシュのAOPアドバイスは同一プロキシ上で両方適用される。
 */
public interface RateMasterRepository extends JpaRepository<RateMaster, Long> {

    @Cacheable(value = "activeRateMasters", key = "'ALL'")
    List<RateMaster> findByActiveTrueOrderByCategoryAscIdAsc();

    @Cacheable(value = "activeRateMasters", key = "#category")
    List<RateMaster> findByCategoryAndActiveTrueOrderByIdAsc(String category);
}
