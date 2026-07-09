# テスト実施結果報告書 (Test Execution Report)

**実施日**: 2026年6月25日（初版）／ 2026年7月9日（Testcontainers導入・全面回帰検証時に更新）
**判定**: **PASS (合格)**

本システム（自動車保険見積サイト）の品質検証のため、バックエンドおよびフロントエンドの自動テストを実施しました。すべてのテストケースが正常に終了し、エラーや失敗がないことを確認しています。

---

## 1. バックエンドテスト結果 (JUnit 5)

バックエンドのテストは、検証対象の深さに応じて **3層** に明確に分離しています。文書上の呼称と実装が一致するよう、各クラスの実態（モックの有無・実データベース接続の有無）をそのまま記載します。

* **実行コマンド**:
  ```bash
  cd backend
  mvn test
  ```

### テスト結果サマリー
* **実行テスト数**: 35 件
* **成功数**: 35 件
* **失敗数**: 0 件
* **エラー**: 0 件
* **スキップ数**: 0 件
* **ビルドステータス**: **BUILD SUCCESS**

### 1.1 単体テスト（Service/Utilクラス単位、DIなし or Mockito）

| テストクラス | テスト件数 | 判定 | 主な検証内容 |
| :--- | :---: | :---: | :--- |
| `PremiumCalculatorTest` | 6 | PASS | 年齢、免許証の色、年間走行距離、運転者範囲に基づく料率計算および基本保険料の乗算チェック。等級・事故有係数、車両保険、人身傷害などの各特約・補償項目の加算・倍率ロジックの正確性を検証。 |
| `JwtTokenProviderTest` | 4 | PASS | カスタムTokenの生成、有効期限内の検証、期限切れエラー、時序攻撃（Timing Attack）防止用の定時署名チェック。 |
| `AdminAuthServiceTest` | 4 | PASS | 管理者ログイン認証における、IDとパスワード（BCryptハッシュ化、モック）の照合、正常時のToken生成、認証エラー時の`UNAUTHORIZED`例外送出、および連続失敗時のログインロックアウト機構（5回失敗でロック、時間経過で解除）の挙動。 |
| `MasterRateServiceTest` | 2 | PASS | 料率マスタ取得（GET `/api/master/rates`）の正常応答ロジック（Repositoryはモック）。 |

### 1.2 Controllerスライステスト（`@WebMvcTest`、Serviceは`@MockBean`）

これらは各Controllerのリクエスト/レスポンス変換・入力バリデーション・HTTPステータスコード・認可制御を検証するテストであり、**実際のデータベース接続やService層の実装は含みません**（Serviceはすべてモック化）。当初「統合テスト」と呼称していましたが、実態を正確に表すためスライステストとして再分類しました。

| テストクラス | テスト件数 | 判定 | 主な検証内容 |
| :--- | :---: | :---: | :--- |
| `QuoteApiIntegrationTest` | 6 | PASS | 一般見積作成（POST `/api/quotes`）の正常系、入力値エラー時の400 Bad Request（VALIDATION_ERROR）と詳細なフィールドエラーメッセージ、見積取得（GET `/api/quotes/{quoteNo}`）の存在有無検証。加えて、数値項目に文字列を渡した場合（`HttpMessageNotReadableException`経由）および整数項目に小数値を渡した場合（Jackson `CoercionConfig`経由）に、いずれも500ではなく400 VALIDATION_ERRORを返すことを検証する回帰テストを含む。 |
| `AdminQuoteApiIntegrationTest` | 4 | PASS | 管理者ポータル用API。見積一覧の取得、検索条件（見積番号、日付範囲）のフィルタリング、CSVエクスポート（GET `/api/admin/quotes.csv`）の正常応答チェック（モック経由の流式出力検証を含む）。 |
| `MasterRateApiSecurityTest` | 2 | PASS | `/api/master/rates` に対する認証保護の検証。未認証でのアクセス制限および認証済みアクセス許可のチェック。 |

### 1.3 結合テスト（`@SpringBootTest` + Testcontainers、実PostgreSQLコンテナ使用）

Serviceをモック化せず、Testcontainersが自動起動する実PostgreSQLコンテナに対してSpring Bootアプリケーション全体（Controller→Service→Repository→DB）を実行する、名実ともに一致した結合テストです。ローカル環境のDocker Compose常駐コンテナには依存せず、Dockerデーモンが利用可能であれば単独で（日本側のCI環境でも）実行可能です。

| テストクラス | テスト件数 | 判定 | 主な検証内容 |
| :--- | :---: | :---: | :--- |
| `QuoteApplicationTests` | 2 | PASS | 実DB接続を含むSpring Bootアプリケーションコンテキストの正常ロード検証、および`db/data.sql`投入データ（料率マスタ）が実データベースへ正しく反映されていることの確認。 |
| `QuoteServiceIntegrationTest` | 2 | PASS | 見積作成API実行→実データベースへの`quotes`/`quote_breakdowns`永続化を直接検証→`GET /api/quotes/{quoteNo}`での再取得一致、という一連の流れをモックなしで検証。加えて、整数項目に小数値を渡した場合に実DBへ一切書き込まれず400を返すことを確認する回帰テスト。 |
| `AdminQuoteServiceIntegrationTest` | 2 | PASS | `db/data.sql`投入済みの実BCryptハッシュを用いた管理者ログイン→実JWT発行→未認証時401→JWT付き管理APIでの実DB検索一致、をモックなしで検証。パスワード誤り時の401エラーも確認。 |
| `RateMasterCachingIntegrationTest` | 1 | PASS | 料率マスタ取得メソッドに付与した`@Cacheable`が、実DBアクセス後にキャッシュへ結果を正しく格納することを確認。 |

---

## 2. フロントエンドテスト結果 (Vitest & React Testing Library)

フロントエンドでは、公開見積フローの主要ステップ画面における入力項目表示、動的バリデーション、およびナビゲーション制御の正確性を検証しました。

* **実行コマンド**:
  ```bash
  cd frontend
  node "./node_modules/vitest/vitest.mjs" run
  ```

### テスト結果サマリー
* **テストファイル数**: 3 ファイル
* **実行テストケース数**: 12 件
* **成功数**: 12 件
* **失敗数**: 0 件
* **判定**: **SUCCESS**

### テスト実行内訳

| テストファイル | テスト件数 | 判定 | 主な検証内容 |
| :--- | :---: | :---: | :--- |
| `StepTop.test.tsx` (SC-001) | 4 | PASS | ・トップ画面のタイトルおよび所要資料ガイドの正常描画。<br>・「見積を始める」ボタン押下時のイベントハンドリング。<br>・見積番号未入力時のバリデーション（日文エラーメッセージの表示）。<br>・存在しない見積番号（404）検索時のエラーハンドリング。 |
| `StepUserInfo.test.tsx` (SC-002) | 4 | PASS | ・年齢、免許証の色、目的、年間走行距離、運転者範囲の入力欄表示。<br>・年齢境界値（18歳未満、100歳超）入力時の日文エラー検証。<br>・走行距離境界値（負数、30000km超）入力時の日文エラー検証。<br>・全項目が有効な場合の「次へ」ボタン有効化と遷移処理。 |
| `StepInsurance.test.tsx` (SC-003) | 4 | PASS | ・他社保険加入有無の選択肢表示。<br>・「加入していない」選択時の即時「次へ」進行可否。<br>・「加入している」選択時の等級・事故有期間入力欄の動的表示連動。<br>・等級（1〜20）および事故有期間（0〜6年）の入力妥当性検証。 |

---

## 3. Docker Compose環境での黒箱回帰確認（2026年7月9日実施）

`docker compose up --build`によるクリーンビルド後、稼働中のコンテナに対して以下をAPI経由で直接確認しました（`mvn test`/`vitest`とは別に、実際に配布される本番同等の環境で再確認）。

| 確認項目 | 結果 |
| :--- | :---: |
| `.env`未設定時の起動失敗、`.env`作成後の`quote-backend`/`quote-db`/`quote-frontend`いずれも healthy | PASS |
| `driverAge: "abc"`（数値項目に文字列）→ 400 VALIDATION_ERROR | PASS |
| `driverAge: 35.5`（整数項目に小数値）→ 400 VALIDATION_ERROR | PASS |
| 正常な見積作成リクエスト → 201 Created | PASS |
| 管理者ログイン、同一IDで5回連続失敗 → 6回目は正しい資格情報でも429 LOGIN_LOCKED | PASS |
| CORSプリフライト（Origin: `http://localhost:5173`）→ `Access-Control-Allow-Origin`等の明示的な応答 | PASS |
| Swagger UI (`/swagger-ui/index.html`) および フロントエンド (`:5173`) の応答 | PASS |
