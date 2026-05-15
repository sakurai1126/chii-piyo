#!/usr/bin/env bash
# OpenAPI Generatorのコード生成スクリプト
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# 入力スキーマ
API_INDEX="$ROOT_DIR/api/index.yml"

# バンドル先のパス
DOCS_YAML="$ROOT_DIR/docs/openapi.yaml"
FRONTEND_YAML="$ROOT_DIR/frontend/openapi.yaml"
BACKEND_YAML="$ROOT_DIR/backend/src/main/resources/openapi.yaml"

# 3箇所へ直接バンドル出力
npx @redocly/cli@latest bundle "$API_INDEX" -o "$DOCS_YAML"
npx @redocly/cli@latest bundle "$API_INDEX" -o "$FRONTEND_YAML"
npx @redocly/cli@latest bundle "$API_INDEX" -o "$BACKEND_YAML"

# バックエンドのコード生成
cd "$ROOT_DIR/backend"
./mvnw clean generate-sources

# フロントエンドのコード生成
cd "$ROOT_DIR/frontend"
npm run generate-api
