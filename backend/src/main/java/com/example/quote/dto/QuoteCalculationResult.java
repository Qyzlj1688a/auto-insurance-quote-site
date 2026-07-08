package com.example.quote.dto;

import com.example.quote.entity.QuoteBreakdown;
import java.util.List;

/**
 * 保険料計算エンジンの結果（年間/月額保険料および内訳）を保持するドメイン層クラス。
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
