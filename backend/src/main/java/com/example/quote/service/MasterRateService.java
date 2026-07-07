package com.example.quote.service;

import com.example.quote.dto.response.RateMasterResponse;

import java.util.List;

/**
 * Rate master service contract.
 */
public interface MasterRateService {

    List<RateMasterResponse> getRates(String category);
}
