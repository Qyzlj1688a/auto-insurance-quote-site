package com.example.quote.dto;

import com.example.quote.entity.QuoteBreakdown;
import java.util.List;

/**
 * Domain-level calculation result carrier.
 */
public class QuoteCalculationResult {

    private final Integer annualPremium;
    private final Integer monthlyPremium;
    private final List<QuoteBreakdown> breakdowns;

    public QuoteCalculationResult(Integer annualPremium, Integer monthlyPremium, List<QuoteBreakdown> breakdowns) {
        this.annualPremium = annualPremium;
        this.monthlyPremium = monthlyPremium;
        this.breakdowns = breakdowns;
    }

    public Integer getAnnualPremium() {
        return annualPremium;
    }

    public Integer getMonthlyPremium() {
        return monthlyPremium;
    }

    public List<QuoteBreakdown> getBreakdowns() {
        return breakdowns;
    }
}
