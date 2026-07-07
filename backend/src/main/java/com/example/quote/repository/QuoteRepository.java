package com.example.quote.repository;

import com.example.quote.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Quote repository.
 */
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    Optional<Quote> findByQuoteNo(String quoteNo);

    @Query(value = "SELECT pg_advisory_xact_lock(:lockKey)", nativeQuery = true)
    void acquireAdvisoryXactLock(@Param("lockKey") long lockKey);

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(quote_no FROM 12 FOR 4) AS INTEGER)), 0) FROM quotes WHERE quote_no LIKE CONCAT('EST', :dateStr, '%')", nativeQuery = true)
    int getMaxSerialForDate(@Param("dateStr") String dateStr);

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime start, LocalDateTime end);

    @Query("SELECT q FROM Quote q WHERE " +
           "q.quoteNo LIKE CONCAT('%', :quoteNo, '%') AND " +
           "q.createdAt >= :fromDate AND " +
           "q.createdAt <= :toDate " +
           "ORDER BY q.createdAt DESC")
    List<Quote> searchQuotes(@Param("quoteNo") String quoteNo,
                            @Param("fromDate") LocalDateTime fromDate,
                            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT q FROM Quote q WHERE " +
           "q.quoteNo LIKE CONCAT('%', :quoteNo, '%') AND " +
           "q.createdAt >= :fromDate AND " +
           "q.createdAt <= :toDate")
    org.springframework.data.domain.Page<Quote> searchQuotesPaged(
            @Param("quoteNo") String quoteNo,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            org.springframework.data.domain.Pageable pageable);
}

