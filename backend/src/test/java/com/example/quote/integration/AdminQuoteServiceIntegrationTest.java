package com.example.quote.integration;

import com.example.quote.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理者ログイン→JWT発行→保護された見積検索APIアクセスまでを、
 * 実際のPostgreSQL（Testcontainers、db/data.sqlで投入されたBCryptハッシュ済みadmin_usersレコード）
 * とSpring Securityの本番設定を通して検証する結合テスト。
 *
 * <p>{@link com.example.quote.controller.AdminQuoteApiIntegrationTest} は
 * {@code @WebMvcTest}（Serviceは{@code @MockBean}）であり、実際のBCrypt照合・JWT発行検証・
 * 実DB検索は行っていない。本クラスはそのギャップを補う。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AdminQuoteServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void adminLogin_withRealBcryptHash_thenAccessProtectedSearchApi_withRealDatabase() throws Exception {
        // 1. db/data.sql で投入された実アカウント（BCryptハッシュ照合）でログインし、本物のJWTを取得する
        String loginResponseJson = mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "admin",
                                  "password": "Admin123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("admin"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponseJson).get("token").asText();

        // 2. 発行されたJWTを使って、まず新規見積を1件作成する（実DBへの永続化）
        String createResponseJson = mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "driverAge": 40,
                                  "licenseColor": "BLUE",
                                  "usageType": "COMMUTE",
                                  "annualMileage": 12000,
                                  "driverRange": "FAMILY",
                                  "hasCurrentInsurance": false,
                                  "maker": "ホンダ",
                                  "carName": "フィット",
                                  "firstRegistrationYearMonth": "2019-03",
                                  "vehicleType": "COMPACT",
                                  "vehicleInsurance": true,
                                  "propertyDamageLimit": "UNLIMITED",
                                  "personalInjuryAmount": "UNLIMITED",
                                  "lawyerOption": true,
                                  "roadService": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode createdBody = objectMapper.readTree(createResponseJson);
        String quoteNo = createdBody.get("quoteNo").asText();

        // 3. JWTなしでの管理APIアクセスは401（未認証）で拒否されることを確認
        mockMvc.perform(get("/api/admin/quotes").param("quoteNo", quoteNo))
                .andExpect(status().isUnauthorized());

        // 4. JWT付きで管理APIへアクセスし、実DBから今作成した見積が検索できることを確認
        mockMvc.perform(get("/api/admin/quotes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("quoteNo", quoteNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quoteNo").value(quoteNo))
                .andExpect(jsonPath("$[0].maker").value("ホンダ"));
    }

    @Test
    void adminLogin_returns401_whenPasswordIsWrong() throws Exception {
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "admin",
                                  "password": "WrongPassword123!"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
