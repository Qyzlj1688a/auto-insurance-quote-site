package com.example.quote.repository;

import com.example.quote.entity.QuoteBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Quote breakdown repository.
 */
public interface QuoteBreakdownRepository extends JpaRepository<QuoteBreakdown, Long> {

    List<QuoteBreakdown> findByQuoteIdOrderByDisplayOrderAsc(Long quoteId);

    /**
     * 指定された見積IDリストに合致するすべての内訳データを一括で取得します（N+1問題の解消用）。
     *
     * @param quoteIds 見積IDリスト
     * @return ソート済みの内訳リスト
     */
    List<QuoteBreakdown> findByQuoteIdInOrderByDisplayOrderAsc(List<Long> quoteIds);
}

