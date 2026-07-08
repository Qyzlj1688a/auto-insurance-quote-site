package com.example.quote.service;

import com.example.quote.dto.response.RateMasterResponse;

import java.util.List;

/**
 * 保険料率マスタサービスインターフェース。
 */
public interface MasterRateService {

    List<RateMasterResponse> getRates(String category);
}
