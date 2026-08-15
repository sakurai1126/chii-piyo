# Chii-Piyo

## 概要

自身の家族内で使用する子育て記録・思い出共有アプリケーション。
写真・動画の共有管理機能、育児記録管理機能、祖父母世代向けのかんたん閲覧モードを実装。
今後AI機能を活用した子育て支援機能の拡充を予定。

## 開発背景

子供の写真を家族間で共有するにあたり、家族内で定着するアプリやサービスがなかなか見つからなかったこと、また特にスマホ操作に疎い祖父母世代との共有では既存アプリの操作が複雑で共有しづらいという課題があった。
一方で自分と妻は育児記録やアルバム管理など、他アプリにまたがる管理を一元化できる高機能なツールを求めた。
極力不要な機能を省き、祖父母にはシンプルなUIにしつつ、自分たちの要望・要件を両立させるには既製品では限界があると感じた。
また、子供の成長に合わせて今後も新しい要件が出てくることが想定される。

その都度機能を拡張していくことで自身の家族に最適化されたアプリを長期的に育てていくこと。
合わせて、各種要件定義・設計、デザイン、バックエンド・フロントエンド実装、インフラ整備、運用改善を一貫して経験することで自身の技術力向上も継続できると考えオーダーメイドでの制作を選択。

## 開発状況

現在後述のPhase1実装まで完了済み。
PWA対応とGitHub ActionsによるCI/CDの設定、およびAWSデプロイが直近の予定。

## スクリーンショット

大まかに画面構成を示すものであり、より詳細なデザインについては [Figmaの画面設計](https://www.figma.com/design/v2lAv2ROvhmWhJJk2csHhs/Chii-Piyo?m=auto&t=CbgxIJ3xj0RROpLI-6) を参照
※Figmaは設計段階のものであるため、実際の画面と細部は異なる

メディア管理関連ページ
![メディア管理](docs/images/pc-2.webp)

<details>
<summary>育児記録関連ページ</summary>

![育児記録関連](docs/images/pc-3.webp)
</details>

<details>
<summary>トップ・設定・エラー画面等</summary>

![トップ・設定・エラー画面等](docs/images/pc-1.webp)
</details>

<details>
<summary>スマホ表示一部</summary>

![スマホ表示一部](docs/images/mobile.webp)
</details>

<details>
<summary>ダークモードデザイン一部</summary>

![ダークモードデザイン一部](docs/images/dark.webp)
</details>

<details>
<summary>かんたんモード一部</summary>

![かんたんモード一部](docs/images/easy.webp)
</details>

## 機能一覧

### Phase 1 コア機能
- 写真/動画のアップロード・管理
- 写真/動画へのコメント機能
- タグ管理・アルバム機能
- お気に入り機能
- 30日保持のゴミ箱機能
- 共有範囲の設定機能
- タイムライン形式の育児記録
- ミルク・排泄・食事・体調のワンタップ記録
- 身体測定（身長・体重）記録・発育グラフ表示
- 「はじめて記録」「ことばの記録」
- マルチログイン・権限管理
- ダークモード対応
- 祖父母向けかんたんモード
- PWA対応

### Phase 2 AI機能
- 排泄パターン分析
- 週次・月次サマリー自動生成
- 育児記録に応じたAI相談
- AI自動タグ付け

### Phase 3 追加機能
- 写真の比較表示機能
- 成長比較表示
- 歯の生え始め記録
- 動画の簡易編集

## 各種設計ドキュメント

- [アーキテクチャ設計書](docs/architecture.md)
- [テーブル定義書](docs/table-definition.md)
- [ER図](docs/er-diagram.md)
- [画面設計書](docs/screen-spec.md)
- [API仕様](docs/openapi.yaml)
- [シーケンス図](docs/sequence.md)
- [テスト設計書](docs/test-plan.md)
- [テストケース表](docs/test-cases/index.md)

## 技術スタック

※Phase1実装完了現在において未対応のCI/CD、インフラ、AIについては導入予定のものを示す

| カテゴリ | 技術 | 選定理由 |
| ---------------- | ----------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| フロントエンド | Next.js 16 / React 19 / TypeScript | TypeScriptの型安全性により開発時のバグを早期に検出できるため<br>育児記録のワンタップ入力や写真アルバムの閲覧などページ遷移を挟まない滑らかな操作体験が求められるアプリケーションであるためSPA/SSRのアプローチからUXを向上させるため<br>市場シェアが高く長期的に知識をアップデートしやすいNext.jsを採用 |
| バックエンド | Spring Boot 4.1.0 / Java 25 | フロントエンドの選定基準同様に市場シェアが高く、情報やライブラリが充実しており長期的な知識のアップデートがしやすいため<br>Java Gold取得を通じてバックエンドでは最も学習強度の高い言語であり、実務でも使用しているため知識を整理・深化する目的で採用 |
| API設計 | OpenAPI Generator | 過去の開発で手書きコードの保守性に課題を感じた経験から、自動生成による画一的なコード管理で長期的な保守性を確保するためにスキーマ駆動開発のアプローチを採用<br>また、実務で使用している技術の理解を深める目的も兼ねている |
| ORM | MyBatis Generator | 上記と同様に自動生成によるコードの画一化で保守性を確保するため<br>SQLを直接制御できるためパフォーマンスチューニングが容易であるため<br>実務で使用している技術の理解を深める目的も兼ねて |
| DB | PostgreSQL | Spring Boot / Javaとの組み合わせで広く採用されており情報が豊富であるため |
| ストレージ | AWS S3 | インフラをAWSで統一し、サービス間の連携と運用管理を集約するため<br>写真・動画のストレージとして高い耐久性と可用性を備えているため |
| インフラ(バックエンド) | AWS Lightsail | 親族内で公開するアプリのため高トラフィックの想定はなく、オーバーエンジニアリングを回避し、長期運用を前提として固定料金で低コストに運用できるため |
| インフラ(フロントエンド) | AWS Amplify | インフラをAWSで統一し一元管理するため<br>Next.jsのSSRに対応しておりApp Routerも利用可能であるため<br>家族内利用の規模であれば無料枠内で運用可能と判断 |
| 認証 | AWS Cognito / Spring Security | インフラをAWSで統一し認証基盤の運用負荷を軽減するため<br>親族間のみの利用想定のため新規登録機能は設けずユーザーはAWS上での手動管理とすることでセキュリティリスクを最小化する目的 |
| AI | Gemini API | 無料枠が充実しており低コストで運用可能であるため<br>精度に関してはリリース後、利用フィードバックを踏まえて他サービスへの変更も検討予定 |
| CSS | TailwindCSS | 別プロジェクトにてCSS Modulesで対応した際にファイル数の増加による管理コストが課題となった経験から、過去利用経験がありユーティリティファーストのアプローチをとれるTailwindCSSを採用 |
| CI/CD | GitHub Actions | 他サービスと比べGitHubとの連携で運用コストが低く、無料枠も充実しているため |
| コンテナ | Docker | ローカル開発環境とデプロイ環境の差異をなくし、環境起因の不具合を防止するため |
| E2Eテスト | Playwright | 直近の実務経験があり、知識を整理・深化する目的で採用<br>Seleniumと比較しWebDriverを介さずブラウザに直接接続するためテストの安定性が高いため |
| ユニットテスト(フロントエンド) | Vitest | 実務のAngular環境でJestとMSWの相性問題を耳にした経験から、ESModulesにネイティブ対応しているVitestを選択<br>JestからVitestへのシェア移行が進んでおり、長期運用の観点でもVitestが有力と判断 |
| ユニットテスト(バックエンド) | JUnit | Java/Spring Bootの標準的なテストフレームワークであるため |
| 静的解析 | SonarQube | コードの品質と脆弱性を継続的に検証するため<br>これも実務にて使用しているため理解を深める意味も兼ねて採用 |

## テストコード実装状況

※Phase1実装完了時点

| 区分 | 件数 |
| --- | --- |
| バックエンド 単体テスト | 303 |
| バックエンド 結合テスト | 17 |
| フロントエンド ユニットテスト | 72 |
| E2Eテスト | 11 |
| 合計 | 403 |

## ディレクトリ構成

```
chii-piyo/
├── api/ # OpenAPI定義
│ ├── components/ # スキーマ・パラメータ等の共通定義
│ ├── paths/ # エンドポイント定義
│ └── index.yml # バンドルのエントリーポイント
├── backend/ # Spring Boot
├── frontend/ # Next.js
├── docs/ # 設計ドキュメント
├── script/ # API定義のバンドル・コード生成スクリプト
└── compose.yml # バックエンド・DBのコンテナ定義
```

### フロントエンド

Bulletproof Reactの設計思想をベースに機能単位でコードを分割

```
frontend/
├── src/
│   ├── app/ # ルーティング・レイアウト
│   │   ├── (auth)/ # 未認証時のルートグループ
│   │   └── (main)/ # 認証後のルートグループ
│   │       └── @modal/ # Parallel Routes / Intercepting Routesによるモーダルでのページ遷移用
│   ├── features/ # 機能別モジュール
│   ├── components/ # 機能横断の共通コンポーネント
│   │   ├── layout/
│   │   └── ui/
│   ├── hooks/ # 共通hooks
│   ├── lib/
│   │   ├── api-client/ # OpenAPI Generator生成のAPIクライアント
│   │   └── auth/ # Cognito連携・セッション管理
│   ├── utils/ # 共通ユーティリティ
│   ├── styles/
│   └── proxy.ts
├── e2e/ # Playwrightテスト
│   └── pages/ # ページオブジェクトモデルでのページ管理
├── test/
│   └── mocks/ # テスト用モック
└── public/ # 静的ファイル
```

featuresの各機能モジュールの内部構造は以下の構成イメージ

```
features/media/
├── actions/
├── api/
├── components/
├── hooks/
└── index.ts # 公開インターフェース、外部から呼び出すものはここに定義
```

### バックエンド

Spring Bootの標準的な三層アーキテクチャに、自動生成用のModelレイヤーと認証・設定のレイヤーを加えた構成

```
backend/src/main/java/link/s_repo/chii_piyo/
├── controller/ # OpenAPI Generator生成インターフェースの実装
├── service/ # ビジネスロジック
├── repository/ # MyBatis Generator生成のMapper・拡張Mapper
├── model/ # 自動生成のEntity・DTO
├── security/ # JWT検証・認可
├── config/ # 各種設定
├── component/ # S3連携等のコンポーネント
├── scheduler/ # 定期実行処理
├── exception/ # 共通例外定義
└── common/ # 共通レスポンス・例外ハンドラ
```


## 環境構築手順

### 必要な環境
- Docker Desktop
- Node.js v24.x.x
- npm v11.x.x

### 手順

**リポジトリのクローン**
```bash
git clone https://github.com/sakurai1126/chii-piyo.git
cd chii-piyo
```

**環境変数の設定**
```bash
cp .env.example .env
```
`.env` を開き各値を設定する。

```bash
cp frontend/.env.example frontend/.env.local
```
`frontend/.env.local` を開き各値を設定する。

**バックエンド起動**
```bash
docker compose up
```

※以降別ターミナルで実行。

**依存パッケージのインストール**
```bash
npm install
cd frontend
npm install
```

**フロントエンド起動**
```bash
npm run dev
```

**動作確認**
- バックエンド
以下のURLにアクセスしてステータスが`UP`であることを確認する。
http://localhost:8080/actuator/health

- フロントエンド
以下のURLにアクセスしてページが表示されることを確認する。
http://localhost:3000


## 開発フロー

### スキーマ駆動開発

API定義は`api/`配下のYAMLファイル群で管理し、`api/index.yml`をエントリーポイントとして集約。
集約後の`openapi.yaml`をフロントエンド・バックエンド・ドキュメントの3箇所に配置しそれぞれのコードを自動生成することで、API定義と実装の乖離を防止。

### APIコード再生成

API定義を変更した際は以下のスクリプトを実行する。

```bash
./script/generate-api.sh
```

内部のスクリプトは以下を順に実行する。

- `index.yml`を起点に統合された`openapi.yaml`を生成し3箇所へ配置
- バックエンドのAPIインターフェースとモデルクラスを再生成
- フロントエンドのTypeScript型+クライアントを再生成

### テスト実行コマンド

```bash
# フロントエンド ユニットテスト
cd frontend && npm test

# E2Eテスト
cd frontend && npm run test:e2e

# バックエンド
cd backend && ./mvnw test
```
