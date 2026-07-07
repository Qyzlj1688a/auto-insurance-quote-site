package com.example.quote.service;

import com.example.quote.dto.response.RateMasterResponse;
import com.example.quote.entity.RateMaster;
import com.example.quote.repository.RateMasterRepository;
import com.example.quote.service.impl.MasterRateServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MasterRateServiceTest {

    private final RateMasterRepository rateMasterRepository = mock(RateMasterRepository.class);
    private final MasterRateService masterRateService = new MasterRateServiceImpl(rateMasterRepository);

    @Test
    void getRatesReturnsAllActiveRatesWhenCategoryIsBlank() {
        RateMaster entity = createRateMaster("LICENSE", "GOLD", "ゴールド", new BigDecimal("0.900"), null);
        when(rateMasterRepository.findByActiveTrueOrderByCategoryAscIdAsc()).thenReturn(List.of(entity));

        List<RateMasterResponse> responses = masterRateService.getRates(null);

        assertEquals(1, responses.size());
        assertEquals("LICENSE", responses.get(0).getCategory());
        assertEquals("GOLD", responses.get(0).getItemCode());
        assertEquals("ゴールド", responses.get(0).getItemName());
    }

    @Test
    void getRatesFiltersByCategoryWhenCategoryIsSpecified() {
        RateMaster entity = createRateMaster("AGE", "AGE_35_59", "35歳〜59歳", new BigDecimal("1.000"), null);
        when(rateMasterRepository.findByCategoryAndActiveTrueOrderByIdAsc("AGE")).thenReturn(List.of(entity));

        List<RateMasterResponse> responses = masterRateService.getRates("AGE");

        assertEquals(1, responses.size());
        assertEquals("AGE_35_59", responses.get(0).getItemCode());
        assertEquals(new BigDecimal("1.000"), responses.get(0).getRate());
    }

    private RateMaster createRateMaster(String category, String itemCode, String itemName, BigDecimal rate, Integer amount) {
        RateMaster entity = new RateMaster();
        entity.setCategory(category);
        entity.setItemCode(itemCode);
        entity.setItemName(itemName);
        entity.setRate(rate);
        entity.setAmount(amount);
        entity.setActive(true);
        return entity;
    }
}
