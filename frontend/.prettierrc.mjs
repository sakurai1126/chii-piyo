const config = {
  // 文末にセミコロンをつける
  semi: true,
  // ダブルクォーテーションを使用する
  singleQuote: false,
  // タブ幅はスペース2つ
  tabWidth: 2,
  // 行末のカンマをすべてつける
  trailingComma: "all",
  // JSXのプロパティはダブルクォーテーションを使用する
  jsxSingleQuote: false,
  // 行の最大幅は100文字
  printWidth: 100,
  // TailwindCSSのクラス名の自動整列プラグイン
  plugins: ["prettier-plugin-tailwindcss"],
};

export default config;
