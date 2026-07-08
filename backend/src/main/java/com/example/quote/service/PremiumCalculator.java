package com.example.quote.service;

import com.example.quote.dto.QuoteCalculationResult;
import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.entity.RateMaster;
import java.util.List;

/**
 * 保険料計算エンジンインターフェース。
 */
public interface PremiumCalculator {

    /**
     * 見積依頼条件と有効な料率リストに基づき、保険料および計算内訳を算出します。
     *
     * @param request 見積作成依頼パラメータ
     * @param rates   データベースからロードされた有効な保険料率マスタのリスト
     * @return 保険料額および計算内訳レコードを含む計算結果
     */
    QuoteCalculationResult calculate(QuoteCreateRequest request, List<RateMaster> rates);
}
