package com.example.quote.repository;

import com.example.quote.entity.RateMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Rate master repository.
 */
public interface RateMasterRepository extends JpaRepository<RateMaster, Long> {

    List<RateMaster> findByActiveTrueOrderByCategoryAscIdAsc();

    List<RateMaster> findByCategoryAndActiveTrueOrderByIdAsc(String category);
}
