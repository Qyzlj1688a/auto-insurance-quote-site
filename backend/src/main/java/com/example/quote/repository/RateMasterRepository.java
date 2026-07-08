package com.example.quote.repository;

import com.example.quote.entity.RateMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 保険料率マスタ用リポジトリインターフェース。
 */
public interface RateMasterRepository extends JpaRepository<RateMaster, Long> {

    List<RateMaster> findByActiveTrueOrderByCategoryAscIdAsc();

    List<RateMaster> findByCategoryAndActiveTrueOrderByIdAsc(String category);
}
