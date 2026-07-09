package com.example.quote.controller;

import com.example.quote.config.SecurityConfig;
import com.example.quote.dto.response.RateMasterResponse;
import com.example.quote.security.AdminUserDetailsService;
import com.example.quote.security.JwtTokenProvider;
import com.example.quote.security.RestAccessDeniedHandler;
import com.example.quote.security.RestAuthenticationEntryPoint;
import com.example.quote.service.MasterRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code MasterRateController} のControllerスライステスト（{@code @WebMvcTest}）。
 * {@link com.example.quote.service.MasterRateService} は {@code @MockBean} でモック化されており、
 * 認可ルール（未認証401 / 認証済み200）のみを検証する。実DBを用いた料率取得の検証は
 * {@code MasterRateServiceTest} を参照。
 */
@WebMvcTest(MasterRateController.class)
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
class MasterRateApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MasterRateService masterRateService;

    @MockBean
    private AdminUserDetailsService adminUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getRatesReturns401WhenUnauthenticated() throws Exception {
        // Assert that unauthenticated requests to GET /api/master/rates are blocked with 401
        mockMvc.perform(get("/api/master/rates"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getRatesReturns200WhenAuthenticated() throws Exception {
        // Assert that authenticated admin requests are allowed
        when(masterRateService.getRates(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/master/rates"))
                .andExpect(status().isOk());
    }
}
