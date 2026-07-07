package com.example.quote.service;

import com.example.quote.dto.QuoteCalculationResult;
import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.entity.RateMaster;
import java.util.List;

/**
 * Premium calculation engine interface.
 */
public interface PremiumCalculator {

    /**
     * Calculates premium and breakdowns based on user request and active rates list.
     *
     * @param request the quote creation conditions
     * @param rates   the active rate masters list loaded from database
     * @return the calculation result containing premiums and breakdown records
     */
    QuoteCalculationResult calculate(QuoteCreateRequest request, List<RateMaster> rates);
}
