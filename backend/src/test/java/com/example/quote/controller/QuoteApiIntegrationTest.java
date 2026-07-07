package com.example.quote.controller;

import com.example.quote.dto.request.QuoteCreateRequest;
import com.example.quote.dto.response.QuoteResultResponse;
import com.example.quote.exception.BusinessException;
import com.example.quote.security.AdminUserDetailsService;
import com.example.quote.security.RestAccessDeniedHandler;
import com.example.quote.security.RestAuthenticationEntryPoint;
import com.example.quote.service.QuoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.quote.config.SecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(QuoteController.class)
@Import(SecurityConfig.class)
class QuoteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private AdminUserDetailsService adminUserDetailsService;

    @MockBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @MockBean
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @MockBean
    private com.example.quote.security.JwtTokenProvider jwtTokenProvider;

    @Test
    void createQuoteReturns201AndDetailsWhenRequestIsValid() throws Exception {
        // IT-001: 正常見積作成テスト
        QuoteCreateRequest request = new QuoteCreateRequest();
        request.setDriverAge(35);
        request.setLicenseColor("GOLD");
        request.setUsageType("PRIVATE");
        request.setAnnualMileage(8000);
        request.setDriverRange("SELF");
        request.setHasCurrentInsurance(false);
        request.setMaker("トヨタ");
        request.setCarName("プリウス");
        request.setFirstRegistrationYearMonth("2020-05");
        request.setVehicleType("SEDAN");
        request.setVehicleInsurance(false);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("UNLIMITED");
        request.setLawyerOption(false);
        request.setRoadService(false);

        QuoteResultResponse mockResponse = new QuoteResultResponse();
        mockResponse.setQuoteNo("EST202606230001");
        mockResponse.setAnnualPremium(80000);
        mockResponse.setMonthlyPremium(6700);
        mockResponse.setCreatedAt("2026-06-23T11:30:00+09:00");

        QuoteResultResponse.BreakdownResponse breakdown = new QuoteResultResponse.BreakdownResponse();
        breakdown.setItemCode("BASE");
        breakdown.setItemName("基本保険料");
        breakdown.setRate(java.math.BigDecimal.valueOf(1.0));
        breakdown.setAmount(50000);
        breakdown.setDisplayOrder(1);
        mockResponse.setBreakdowns(List.of(breakdown));

        when(quoteService.createQuote(any(QuoteCreateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteNo").value("EST202606230001"))
                .andExpect(jsonPath("$.annualPremium").value(80000))
                .andExpect(jsonPath("$.monthlyPremium").value(6700))
                .andExpect(jsonPath("$.breakdowns[0].itemCode").value("BASE"))
                .andExpect(jsonPath("$.breakdowns[0].itemName").value("基本保険料"))
                .andExpect(jsonPath("$.createdAt").value("2026-06-23T11:30:00+09:00"));
    }

    @Test
    void createQuoteReturns400ValidationErrorWhenRequestIsInvalid() throws Exception {
        // IT-002: フィールド欠落・範囲エラーテスト
        QuoteCreateRequest request = new QuoteCreateRequest();
        // driverAge is under 18 (invalid)
        request.setDriverAge(17);
        request.setLicenseColor("GOLD");
        request.setUsageType("PRIVATE");
        request.setAnnualMileage(8000);
        request.setDriverRange("SELF");
        request.setHasCurrentInsurance(false);
        request.setMaker("トヨタ");
        request.setCarName("プリウス");
        request.setFirstRegistrationYearMonth("2020-05");
        request.setVehicleType("SEDAN");
        request.setVehicleInsurance(false);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("UNLIMITED");
        request.setLawyerOption(false);
        request.setRoadService(false);

        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("入力内容に誤りがあります。"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("driverAge"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("年齢は18〜100歳の間で入力してください。"));
    }

    @Test
    void getQuoteReturns200AndDetailsWhenQuoteNoExists() throws Exception {
        // IT-003: 存在する見積番号の取得テスト
        QuoteResultResponse mockResponse = new QuoteResultResponse();
        mockResponse.setQuoteNo("EST202606230001");
        mockResponse.setAnnualPremium(80000);
        mockResponse.setMonthlyPremium(6700);
        mockResponse.setCreatedAt("2026-06-23T11:30:00+09:00");

        QuoteResultResponse.BreakdownResponse breakdown = new QuoteResultResponse.BreakdownResponse();
        breakdown.setItemCode("BASE");
        breakdown.setItemName("基本保険料");
        breakdown.setRate(java.math.BigDecimal.valueOf(1.0));
        breakdown.setAmount(50000);
        breakdown.setDisplayOrder(1);
        mockResponse.setBreakdowns(List.of(breakdown));

        when(quoteService.getQuoteByQuoteNo("EST202606230001")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/quotes/EST202606230001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteNo").value("EST202606230001"))
                .andExpect(jsonPath("$.annualPremium").value(80000))
                .andExpect(jsonPath("$.monthlyPremium").value(6700))
                .andExpect(jsonPath("$.breakdowns[0].itemCode").value("BASE"))
                .andExpect(jsonPath("$.createdAt").value("2026-06-23T11:30:00+09:00"));
    }

    @Test
    void getQuoteReturns404NotFoundWhenQuoteNoDoesNotExist() throws Exception {
        // IT-004: 存在しない見積番号の取得テスト
        when(quoteService.getQuoteByQuoteNo("EST999912319999"))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", "指定された見積番号は存在しません。"));

        mockMvc.perform(get("/api/quotes/EST999912319999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("指定された見積番号は存在しません。"));
    }
}
