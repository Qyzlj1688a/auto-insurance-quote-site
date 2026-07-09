package com.example.quote.integration;

import com.example.quote.AbstractIntegrationTest;
import com.example.quote.entity.Quote;
import com.example.quote.entity.QuoteBreakdown;
import com.example.quote.repository.QuoteBreakdownRepository;
import com.example.quote.repository.QuoteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Serviceをモック化せず、Testcontainersが起動する実PostgreSQLに対して
 * 「見積作成 → DB直接検証 → API経由での再取得」までを一貫して確認する真の結合テスト。
 *
 * <p>{@link com.example.quote.controller.QuoteApiIntegrationTest} は
 * {@code @WebMvcTest}（Controllerスライステスト、Serviceはモック）であり、
 * 実際のDB永続化・トランザクション・見積番号の採番ロジックまでは検証していない。
 * 本クラスはそのギャップを補うことを目的とする。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class QuoteServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private QuoteBreakdownRepository quoteBreakdownRepository;

    private static String validRequestJson(int driverAge) {
        return """
                {
                  "driverAge": %d,
                  "licenseColor": "GOLD",
                  "usageType": "PRIVATE",
                  "annualMileage": 8000,
                  "driverRange": "SELF",
                  "hasCurrentInsurance": false,
                  "maker": "トヨタ",
                  "carName": "プリウス",
                  "firstRegistrationYearMonth": "2020-05",
                  "vehicleType": "SEDAN",
                  "vehicleInsurance": false,
                  "propertyDamageLimit": "UNLIMITED",
                  "personalInjuryAmount": "UNLIMITED",
                  "lawyerOption": false,
                  "roadService": false
                }
                """.formatted(driverAge);
    }

    @Test
    void createQuote_persistsToRealDatabase_andCanBeRetrievedViaApi() throws Exception {
        // 1. 見積作成APIを実行（Serviceは実装のまま、DBも実PostgreSQL）
        String createResponseJson = mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson(35)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteNo").isNotEmpty())
                .andExpect(jsonPath("$.annualPremium").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createdBody = objectMapper.readTree(createResponseJson);
        String quoteNo = createdBody.get("quoteNo").asText();
        int annualPremium = createdBody.get("annualPremium").asInt();

        // 2. リポジトリ経由で直接DBを検証（見積番号採番・カラム値の永続化を確認）
        Optional<Quote> persisted = quoteRepository.findByQuoteNo(quoteNo);
        assertThat(persisted).isPresent();
        Quote quote = persisted.get();
        assertThat(quote.getDriverAge()).isEqualTo(35);
        assertThat(quote.getMaker()).isEqualTo("トヨタ");
        assertThat(quote.getCarName()).isEqualTo("プリウス");
        assertThat(quote.getAnnualPremium()).isEqualTo(annualPremium);
        assertThat(quote.getQuoteNo()).matches("^EST\\d{12}$");

        // 3. 内訳(quote_breakdowns)も同一トランザクションで実際に保存されていることを確認
        List<QuoteBreakdown> breakdowns = quoteBreakdownRepository.findByQuoteIdOrderByDisplayOrderAsc(quote.getId());
        assertThat(breakdowns).isNotEmpty();
        // PremiumCalculatorImplはitemCodeを"category_itemCode"形式で保存する（例: BASE_PREMIUM_BASE）
        assertThat(breakdowns).anySatisfy(b -> assertThat(b.getItemCode()).isEqualTo("BASE_PREMIUM_BASE"));

        // 4. GET /api/quotes/{quoteNo} で取得した結果がDB上のデータと一致することを確認
        mockMvc.perform(get("/api/quotes/" + quoteNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteNo").value(quoteNo))
                .andExpect(jsonPath("$.annualPremium").value(annualPremium))
                .andExpect(jsonPath("$.breakdowns", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())));
    }

    @Test
    void createQuote_returns400_andDoesNotPersist_whenDriverAgeHasFractionalPart() throws Exception {
        // 重大問題2: Integer項目に小数値を渡した場合、静默截断で成功させず400を返すことを実DBで再確認
        long countBefore = quoteRepository.count();

        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "driverAge": 35.5,
                                  "licenseColor": "GOLD",
                                  "usageType": "PRIVATE",
                                  "annualMileage": 8000,
                                  "driverRange": "SELF",
                                  "hasCurrentInsurance": false,
                                  "maker": "トヨタ",
                                  "carName": "プリウス",
                                  "firstRegistrationYearMonth": "2020-05",
                                  "vehicleType": "SEDAN",
                                  "vehicleInsurance": false,
                                  "propertyDamageLimit": "UNLIMITED",
                                  "personalInjuryAmount": "UNLIMITED",
                                  "lawyerOption": false,
                                  "roadService": false
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        assertThat(quoteRepository.count()).isEqualTo(countBefore);
    }
}
