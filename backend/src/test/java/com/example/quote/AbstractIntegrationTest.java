package com.example.quote;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Testcontainersで実PostgreSQLコンテナを起動し、Spring Bootの{@code @ServiceConnection}機能で
 * {@code spring.datasource.*} を自動注入する「真の結合テスト」用共通基底クラス。
 *
 * <p>本番のDocker Compose環境に依存せず、ローカル/CI（例: GitHub Actions）のいずれでも
 * Dockerデーモンが利用可能であれば自己完結的に実行できる。コンテナ内では
 * {@code db/schema.sql} と {@code db/data.sql}（本番と全く同一のファイル。
 * {@code pom.xml} の testResources 設定でテストクラスパスへコピーされる）を
 * {@code spring.sql.init} 機構で適用するため、DDLとマスタデータの二重管理を避けている。
 *
 * <p><b>実装上の注意</b>: 本クラスは{@code @Testcontainers}/{@code @Container}による
 * JUnit拡張管理ではなく、static初期化ブロックで手動起動する
 * 「Testcontainers公式のシングルトンコンテナパターン」を採用している。
 * {@code @Testcontainers}を使うと、各テストクラスの{@code afterAll}でコンテナが
 * 停止され、次のテストクラスの{@code beforeAll}で（ホストポートが変わった状態で）
 * 再起動されてしまう。一方、Spring TestContextはApplicationContextを
 * （設定が同一であれば）クラス間でキャッシュ・再利用するため、
 * 「古いポートを指すキャッシュ済みDataSource」と「新しいポートで再起動したコンテナ」の
 * 不整合が発生し、`Connection refused` に繋がる。static初期化ブロックで1度だけ起動し、
 * 明示的に停止しない（JVM終了時にTestcontainers付属のRyukリーパーが自動的に
 * コンテナを破棄する）ことで、この不整合を回避している。
 */
@ActiveProfiles("test")
@Tag("integration")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    static {
        POSTGRES.start();
    }
}
