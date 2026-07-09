package com.example.quote;

import com.example.quote.repository.AdminUserRepository;
import com.example.quote.repository.QuoteRepository;
import com.example.quote.repository.RateMasterRepository;
import com.example.quote.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Bootアプリケーションコンテキストが、実際のPostgreSQL（Testcontainers）に接続した状態で
 * 正常にロードできることを検証する結合テスト。
 *
 * <p>従来はクラス名の文字列比較のみを行う「空心テスト」であったが、本テストでは
 * {@code @SpringBootTest} で全Beanを実際に生成させ、JPAリポジトリ・サービス・
 * セキュリティ設定を含む全構成が破綻なく起動することを保証する。
 */
@SpringBootTest
class QuoteApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private RateMasterRepository rateMasterRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Test
    void contextLoadsWithAllCoreBeansWired() {
        assertThat(applicationContext).isNotNull();
        assertThat(quoteService).isNotNull();
        assertThat(quoteRepository).isNotNull();
        assertThat(rateMasterRepository).isNotNull();
        assertThat(adminUserRepository).isNotNull();
    }

    @Test
    void rateMasterDataIsSeededFromDataSqlIntoRealDatabase() {
        // db/data.sql が実PostgreSQLへ正しく適用され、料率マスタが読み込めることを確認する。
        assertThat(rateMasterRepository.findByActiveTrueOrderByCategoryAscIdAsc()).isNotEmpty();
    }
}
