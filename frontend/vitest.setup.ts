import "@testing-library/jest-dom/vitest";

import { cleanup } from "@testing-library/react";
import { afterEach, vi } from "vitest";

// 各テストケース毎のクリーンアップ処理
// React Testing Libraryのがレンダリングしたコンポーネントツリーをアンマウントして削除
afterEach(() => {
  cleanup();
});

// クライアントコンポーネント内で useRouter 等のフックを呼び出している場合、JSDOM環境ではエラーになるためモック化
vi.mock("next/navigation", () => ({
  // useRouter() の呼び出しに対してモックを返す
  useRouter: () => ({
    // ルーティング遷移
    push: vi.fn(),
    // 履歴を置換する遷移
    replace: vi.fn(),
    // ページのプリフェッチ
    prefetch: vi.fn(),
    // 戻るボタン
    back: vi.fn(),
    // 進むボタン
    forward: vi.fn(),
    // サーバーデータの再取得
    refresh: vi.fn(),
  }),
  // クエリパラメータ取得のモック
  useSearchParams: () => new URLSearchParams(),
  // 現在のパス取得のモック
  usePathname: () => "",
  // パラメータ取得のモック
  useParams: () => ({}),
  // リダイレクト用モック
  redirect: vi.fn(),
}));

// JSDOMは window.matchMedia を実装していないためモック化
Object.defineProperty(window, "matchMedia", {
  // 値を上書きできるように許可
  writable: true,
  // 各処理にモックを返す
  value: (query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  }),
});
