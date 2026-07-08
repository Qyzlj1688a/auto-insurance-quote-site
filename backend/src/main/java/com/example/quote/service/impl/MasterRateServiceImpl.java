package com.example.quote.service.impl;

import com.example.quote.dto.response.RateMasterResponse;
import com.example.quote.entity.RateMaster;
import com.example.quote.repository.RateMasterRepository;
import com.example.quote.service.MasterRateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 保険料率マスタサービス実装クラス。
 */
@Service
public class MasterRateServiceImpl implements MasterRateService {

    private final RateMasterRepository rateMasterRepository;

    public MasterRateServiceImpl(RateMasterRepository rateMasterRepository) {
        this.rateMasterRepository = rateMasterRepository;
    }

    @Override
    public List<RateMasterResponse> getRates(String category) {
        List<RateMaster> rateMasters;
        if (category == null || category.isBlank()) {
            rateMasters = rateMasterRepository.findByActiveTrueOrderByCategoryAscIdAsc();
        } else {
            rateMasters = rateMasterRepository.findByCategoryAndActiveTrueOrderByIdAsc(category);
        }
        return rateMasters.stream()
                .map(RateMasterResponse::from)
                .toList();
    }
}
