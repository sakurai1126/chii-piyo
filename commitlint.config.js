export default {
  extends: ["@commitlint/config-conventional"],
  rules: {
    // 使えるtypeを制限
    "type-enum": [
      2,
      "always",
      ["feat", "fix", "build", "chore", "ci", "docs", "style", "refactor", "perf", "test", "revert"],
    ],
    // descriptionの大文字小文字を制限しない
    "subject-case": [0],
    // descriptionの最大文字数を指定
    "subject-max-length": [2, "always", 72],
  },
};
