# 自動車保険見積システム (Automobile Insurance Quote System)

自動車保険の保険料を簡単に概算し、管理者が登録された見積情報を検索・確認およびCSVエクスポートできるWebアプリケーションシステムです。

---

## 特徴・主な機能

### 1. 公開見積フロー (SC-001 ~ SC-007)
一般利用者が自身の条件、車両情報、補償内容を入力することで、スピーディーに保険料の概算を算出する機能です。
- **SC-001 トップページ**: 見積開始と過去の見積番号による呼び出し。
- **SC-002 使用者情報入力**: 年齢、免許証の色、使用目的、年間走行距離、運転者範囲の入力およびバリデーション。
- **SC-003 契約中保険情報入力**: 現在の保険有無、等級（1〜20等級）、事故有係数適用期間（0〜6年）の入力。
- **SC-004 車両情報入力**: 車種、メーカー、初度登録年月、車両タイプ（軽自動車、SUV、セダン等）の入力。
- **SC-005 補償条件入力**: 対物賠償、人身傷害、弁護士特約、ロードサービスなどの選択。
- **SC-006 入力内容確認**: 条件の要約と概算保険料（年額・月額）の確認。
- **SC-007 見積結果表示**: 見積番号の発行と詳細な計算内訳の確認。

### 2. 管理者ポータル (SC-008 ~ SC-009)
管理者が保管された見積データを検索・閲覧・出力するための管理機能です。
- **SC-008 管理者ログイン**: セキュリティ保護された管理機能への認証。
- **SC-009 見積一覧・検索・詳細表示・CSV出力**: 
  - 見積番号、作成開始日、作成終了日によるフィルタリング検索。
  - 該当データがない場合の案内表示。
  - 各行からモーダルで詳細な見積条件と計算内訳を表示。
  - 検索条件に合致する見積データの一括CSVダウンロード（UTF-8 BOM対応）。

---

## 技術スタックとバージョン基準

本システムは以下の技術スタックとバージョンに基づいて構築されています：

- **Backend**: Java 17, Spring Boot 3.3.2, Spring Data JPA, Spring Security, JJWT
- **Frontend**: React 18.3.1, TypeScript 5.6.3, Vite 5.4.10, Axios 1.7.7
- **Database**: PostgreSQL 16
- **Container**: Docker Compose
- **API ドキュメント**: OpenAPI / Swagger UI (springdoc-openapi)
- **テストフレームワーク**: JUnit 5, Mockito, Testcontainers, Vitest, React Testing Library
- **キャッシュ**: Spring Cache（`ConcurrentMapCacheManager`）による料率マスタ取得のキャッシュ化
- **CI/CD**: GitHub Actions（[.github/workflows/ci.yml](.github/workflows/ci.yml)）でpush/PR時にバックエンド（`mvn test`、Testcontainers結合テスト含む）とフロントエンド（型チェック、Vitest）を自動実行

管理者認証では、JJWTを使用した標準JWT（HS256署名）を発行し、管理者APIへのアクセス時にBearerトークンとして検証します。

---

## ディレクトリ構成

```text
.
├─ backend/             # Spring Boot バックエンドプロジェクト
├─ frontend/            # React フロントエンドプロジェクト
├─ db/                  # データベース初期化SQLスクリプト (schema.sql, data.sql)
└─ docker-compose.yml   # Docker Compose 構成ファイル
```

---

## 起動方法

### 0. 環境変数ファイル（.env）の準備【初回のみ・必須】

JWT署名用シークレットなどの機密情報をリポジトリに直接コミットしないため、
`.env.example` をコピーして `.env` を作成し、値を設定してください（`.env` は `.gitignore` 対象）。

```bash
cp .env.example .env        # Linux/macOS
Copy-Item .env.example .env # Windows PowerShell
```

`.env` 内の `JWT_SECRET` を32文字以上のランダムな文字列に変更してください
（例: `openssl rand -base64 48`）。この手順を行わない場合、`JWT_SECRET` が空文字となり
バックエンドがJWT署名鍵の生成に失敗して起動できません。

### 1. Docker Compose での一括起動（推奨）

プロジェクトのルートディレクトリで以下のコマンドを実行します：

```bash
docker compose up --build -d
```

起動完了後、以下のURLから各サービスにアクセスできます：

- **Frontend (一般見積り)**: `http://localhost:5173`
- **Frontend (管理者ログイン)**: `http://localhost:5173/admin/login`
- **Backend**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Actuator Health Check**: `http://localhost:8080/actuator/health`

### 2. 個別での起動方法

#### データベース (PostgreSQL)
```bash
docker compose up db -d
```

#### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```

#### Frontend (React)
```bash
cd frontend
npm install
npm run dev
```

---

## データベース設計と初期アカウント

`docker compose up` 時、またはDBコンテナ初回起動時に以下のスクリプトが自動実行されます：
- `db/schema.sql`: テーブル作成（`quotes`、`quote_breakdowns`、`rate_masters`、`admin_users` 等）および CHECK 制約の定義。
- `db/data.sql`: 基本料率マスタデータおよび管理者アカウントの登録。

### 管理者初期アカウント情報
- **ログインID**: `admin`
- **パスワード**: `Admin123!`
※上記アカウントを使用し、`http://localhost:5173/admin/login` から管理者システムにログインできます。
※パスワードは `BCrypt` によってハッシュ化され安全に保管されています。

---

## 既知の制限（Known Limitations）

本システムは入社前課題として、要求される主要機能・設計観点を優先的に実装したものであり、
以下の点は本番の商用サービスとしてはスコープ外・簡易実装としている。今後の拡張時に参照すること。

- **見積の編集・削除は不可**: 一度作成した見積は不変（イミュータブル）として扱い、内容を変更したい場合は
  再度見積を作成する運用を前提としている。誤入力時の取消・修正APIは提供していない。
- **管理者ロールは単一**: `admin_users` テーブルはロール区分を持たず、全管理者が同一権限を持つ。
  閲覧専用・編集可能などの権限段階分けは実装していない。
- **ログイン失敗ロックアウトはインメモリ実装**: 同一プロセス内でのみ有効。バックエンドを複数インスタンスで
  水平スケールする場合、インスタンスごとに独立してカウントされるため、厳密なブルートフォース対策としては
  Redis等の外部共有ストアへの置き換えが必要。
- **料率マスタのキャッシュは`ConcurrentMapCacheManager`（プロセス内キャッシュ）**: 管理画面から
  料率マスタを動的に変更する機能は未実装のため、現状はアプリケーション再起動でのみキャッシュが更新される
  想定。将来、料率マスタの管理API・変更を実装する場合は、キャッシュ無効化処理を追加する必要がある。
- **保険料計算ロジックは課題用の簡易ルール**: 実際の損害保険料率算出のような高度な統計モデルではなく、
  基本保険料に複数の係数（年齢・免許証の色・使用目的等）を乗算する単純化されたルールベース計算である。
- **決済・契約機能は対象外**: 本課題は見積（概算）機能のみが対象であり、実際の契約締結・決済処理は
  含まれない。
- **見積結果照会（`GET /api/quotes/{quoteNo}`）は認証不要**: 詳細設計書のAPI-002定義に従い、見積番号を
  知っていれば本人確認なしに結果を参照できる仕様としている。`quote_no`は`ESTyyyyMMdd0001`形式の連番
  であるため、番号を推測・総当たりされた場合、他者の見積条件・保険料が閲覧され得る（氏名・連絡先等の
  個人識別情報は保存していないため、漏洩範囲は保険条件と金額に限られる）。本課題では本人確認（対象外
  事項）を前提としないためこの仕様を採用しているが、本番運用に転用する場合は、番号の非連続化
  （UUID化等）や、発行直後のみ照会可能とする等の追加対策が必要。
- **CORS許可オリジンは開発環境向けデフォルト**: `app.cors.allowed-origins`（既定値
  `http://localhost:5173`）で管理しており、本番相当のドメインで運用する場合は環境変数
  `CORS_ALLOWED_ORIGINS` で上書きすること。

---

## テスト実施結果

本プロジェクトは品質確保のため、バックエンド・フロントエンドの両方で自動テストを整備しています。

### 1. バックエンド自動テスト (JUnit 5 / Testcontainers)
料率計算エンジン、日付検証、バリデーション、例外ハンドリング、見積登録・取得APIを、単体テスト・Controllerスライステスト（Serviceモック）・Testcontainersによる実PostgreSQLを用いた結合テストの3層で網羅しています。

- **実行コマンド**:
  ```bash
  cd backend
  mvn test
  ```
  ※ Testcontainersを用いた結合テストがDockerデーモンに接続してPostgreSQLコンテナを起動するため、
  `mvn test` の実行にはDocker（Docker Desktop等）が稼働している必要があります。
- **テスト結果**:
  - テスト実行数: **35件**（単体テスト16件、Controllerスライステスト12件、Testcontainers結合テスト7件）
  - 成功数: **35件**
  - 失敗数: **0件**
  - エラー: **0件**
  - ビルドステータス: **BUILD SUCCESS**

### 2. フロントエンド自動テスト (Vitest & React Testing Library)
各ステップ画面（見積トップ、使用者情報、契約中保険）の描画、入力バリデーション、およびイベントハンドリングのテストを整備しています。

- **実行コマンド**:
  ```bash
  cd frontend
  node "./node_modules/vitest/vitest.mjs" run
  ```
- **テスト結果**:
  - テストファイル数: **3件**
  - テストケース数: **12件**
  - 成功数: **12件**
  - 失敗数: **0件**
  - 判定: **SUCCESS**

※詳細な検証内容については、プロジェクトに同梱されている [test_results.md](./test_results.md) を参照してください。
