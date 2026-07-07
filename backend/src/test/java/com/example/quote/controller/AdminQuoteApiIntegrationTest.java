package com.example.quote.controller;

import com.example.quote.config.SecurityConfig;
import com.example.quote.dto.request.AdminLoginRequest;
import com.example.quote.dto.response.AdminLoginResponse;
import com.example.quote.dto.response.QuoteResultResponse;
import com.example.quote.security.AdminUserDetailsService;
import com.example.quote.security.RestAccessDeniedHandler;
import com.example.quote.security.RestAuthenticationEntryPoint;
import com.example.quote.service.AdminAuthService;
import com.example.quote.service.QuoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminQuoteController.class, AdminAuthController.class})
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
class AdminQuoteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private AdminAuthService adminAuthService;

    @MockBean
    private AdminUserDetailsService adminUserDetailsService;

    @MockBean
    private com.example.quote.security.JwtTokenProvider jwtTokenProvider;


    @Test
    void adminLoginReturns200AndTokenWhenCredentialsAreValid() throws Exception {
        // IT-005: 管理者ログイン認証成功テスト
        AdminLoginRequest request = new AdminLoginRequest();
        request.setLoginId("admin");
        request.setPassword("Admin123!");

        AdminLoginResponse mockResponse = new AdminLoginResponse();
        mockResponse.setLoginId("admin");
        mockResponse.setDisplayName("管理者");
        mockResponse.setMessage("ログインに成功しました。");
        mockResponse.setToken("mock_jwt_token");

        when(adminAuthService.login(any(AdminLoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("admin"))
                .andExpect(jsonPath("$.displayName").value("管理者"))
                .andExpect(jsonPath("$.message").value("ログインに成功しました。"))
                .andExpect(jsonPath("$.token").value("mock_jwt_token"));
    }

    @Test
    void getAdminQuotesReturns401WhenUnauthenticated() throws Exception {
        // IT-006: 未ログイン（未認証）時の401エラーテスト
        mockMvc.perform(get("/api/admin/quotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin")
    void getAdminQuotesReturns200AndMatchingListWhenAuthenticated() throws Exception {
        // IT-007: ログイン済み状態での検索・フィルタリングテスト
        QuoteResultResponse mockQuote = new QuoteResultResponse();
        mockQuote.setQuoteNo("EST202606230001");
        mockQuote.setAnnualPremium(80000);
        mockQuote.setMonthlyPremium(6700);
        mockQuote.setCreatedAt("2026-06-23T11:30:00+09:00");
        mockQuote.setBreakdowns(Collections.emptyList());

        when(quoteService.searchQuotes("EST202606230001", "2026-06-23", "2026-06-23"))
                .thenReturn(List.of(mockQuote));

        mockMvc.perform(get("/api/admin/quotes")
                        .param("quoteNo", "EST202606230001")
                        .param("createDateFrom", "2026-06-23")
                        .param("createDateTo", "2026-06-23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quoteNo").value("EST202606230001"))
                .andExpect(jsonPath("$[0].annualPremium").value(80000))
                .andExpect(jsonPath("$[0].monthlyPremium").value(6700));
    }

    @Test
    @WithMockUser(username = "admin")
    void exportAdminQuotesCsvReturns200AndCsvDataWithBom() throws Exception {
        // IT-008: CSVエクスポート取得テスト（BOM付きUTF-8, text/csv）
        String mockCsv = "\uFEFF見積番号,作成日時,年間保険料,月額保険料,免許証の色,使用目的\r\n" +
                "EST202606230001,2026-06-23 11:30:00,80000,6700,ゴールド,日常・レジャー\r\n";

        org.mockito.Mockito.doAnswer(invocation -> {
            java.io.Writer writer = invocation.getArgument(0);
            writer.write(mockCsv);
            writer.flush();
            return null;
        }).when(quoteService).exportQuotesCsvStream(any(java.io.Writer.class), any(), any(), any());

        mockMvc.perform(get("/api/admin/quotes.csv")
                        .param("quoteNo", "EST202606230001")
                        .param("createDateFrom", "2026-06-23")
                        .param("createDateTo", "2026-06-23"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv;charset=UTF-8"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"quotes.csv\"; filename*=UTF-8''quotes.csv"))
                .andExpect(content().string(mockCsv));
    }
}
