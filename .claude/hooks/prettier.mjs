// Edit と Write の直後に呼ばれ、編集したファイルを Prettier で整形する。
// コミット時だけ整形すると編集直後の差分が読みにくく、コミット時に大きな整形差分が出るため編集の都度行う。
import { spawnSync } from "node:child_process";
import { existsSync } from "node:fs";
import path from "node:path";

// lint-stagedの対象に揃える。
// yamlは生成されたバンドルを含むため入れない。
const targetExtensions = new Set([".ts", ".tsx", ".mjs", ".js", ".json", ".css", ".md"]);

// フックにClaudeCodeが渡す情報を標準入力から読み込み、chunksに格納する。
const chunks = [];
for await (const chunk of process.stdin) {
  chunks.push(chunk);
}

let filePath;
try {
  // 標準入力から受け取ったJSONをパースし、編集対象ファイルのパスを取得する。
  filePath = JSON.parse(Buffer.concat(chunks).toString("utf8")).tool_input?.file_path;
} catch {
  // 入力が読めなくても編集作業は止めない。
  process.exit(0);
}
if (!filePath) {
  process.exit(0);
}

const projectDir = process.env.CLAUDE_PROJECT_DIR ?? process.cwd(); 
const frontendDir = path.join(projectDir, "frontend");
// 編集対象ファイルのパスをfrontendディレクトリからの相対パスに変換する。
const relative = path.relative(frontendDir, path.resolve(filePath));

// frontend配下以外、または絶対パスのままの場合は対象外として何もしない。
if (relative.startsWith("..") || path.isAbsolute(relative)) {
  process.exit(0);
}
// 対象外の拡張子の場合は何もしない。
if (!targetExtensions.has(path.extname(relative))) {
  process.exit(0);
}

// Prettierの実行ファイルのパスを取得し、存在しなければ何もしない。
const prettierBin = path.join(frontendDir, "node_modules", "prettier", "bin", "prettier.cjs");
if (!existsSync(prettierBin)) {
  process.exit(0);
}

// Prettierを実行して対象ファイルを整形する。
const result = spawnSync(process.execPath, [prettierBin, "--write", relative], {
  cwd: frontendDir,
  encoding: "utf8",
});

// 整形の失敗（Prettierの終了コードが 0 以外）の場合、結果は標準エラー出力に出すだけで正常終了する。
if (result.status !== 0) {
  process.stderr.write(result.stderr || result.stdout || "");
  process.exit(2);
}
process.exit(0);
