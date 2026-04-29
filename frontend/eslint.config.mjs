import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";
import prettier from "eslint-config-prettier";

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  // Prettierのルールを最後に適用して、他のルールと競合する可能性のあるスタイルルールを無効化する
  prettier,

  // Lintの対象外ファイルを指定
  globalIgnores([
    ".next/**",
    "out/**",
    "build/**",
    "next-env.d.ts",
    // OpenAPI Generatorの自動生成コード
    "src/lib/gen/**",
  ]),

  {
    rules: {
      // importの順番をアルファベット順にする
      "import/order": [
        // ルールのレベルをエラーに設定
        "error",
        {
          // インポートのグループ分けと順番を指定
          groups: [
            // Node.js本体に最初から入っているモジュール
            "builtin",
            // npmなどでインストールした外部モジュール
            "external",
            // "@/"などでインポートするプロジェクト内のモジュール
            "internal",
            // 親ディレクトリからの相対パスでのインポート
            "parent",
            // 同じディレクトリ内のインポート
            "sibling",
            // "./"のみでインポートできるindexファイル
            "index",
          ],
          // グループ間に常に改行を入れる
          "newlines-between": "always",
          // 同じグループ内のimportをアルファベット順にソート
          alphabetize: { order: "asc" },
        },
      ],

      // 未使用の変数をエラーにする
      "@typescript-eslint/no-unused-vars": [
        "error",
        // 引数名が_で始まる場合は未使用の引数として許可する
        { argsIgnorePattern: "^_" },
      ],

      // console.logを警告にする ※console.errorとconsole.warnは許可
      "no-console": ["warn", { allow: ["error", "warn"] }],
    },
  },
]);

export default eslintConfig;
