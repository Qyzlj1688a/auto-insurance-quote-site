package com.example.quote.controller;

import com.example.quote.dto.response.RateMasterResponse;
import com.example.quote.service.MasterRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rate master controller.
 */
@RestController
@RequestMapping("/api/master")
public class MasterRateController {

    private final MasterRateService masterRateService;

    public MasterRateController(MasterRateService masterRateService) {
        this.masterRateService = masterRateService;
    }

    @GetMapping("/rates")
    public List<RateMasterResponse> getRates(@RequestParam(required = false) String category) {
        return masterRateService.getRates(category);
    }
}
