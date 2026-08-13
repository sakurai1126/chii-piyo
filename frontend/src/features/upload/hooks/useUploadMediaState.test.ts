import { act, renderHook } from "@testing-library/react";
import { afterAll, beforeAll, describe, expect, it, vi } from "vitest";

import { toast } from "@/components/ui/Toast";

import { useUploadMediaState } from "./useUploadMediaState";

// モックの定義
vi.mock("@/components/ui/Toast", () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

// JSDOMで動作しないブラウザ機能のモック設定
beforeAll(() => {
  // ファイル読み込み時などに使用されるObjectURLをモック定義
  globalThis.URL.createObjectURL = vi.fn().mockReturnValue("blob:http://localhost/mock-url");
  // メモリ解放用の関数をモック定義
  globalThis.URL.revokeObjectURL = vi.fn();

  // Imageオブジェクトをモック定義
  vi.stubGlobal(
    "Image",
    class {
      naturalWidth = 100;
      naturalHeight = 100;
      onload: (() => void) | null = null;
      set src(_: string) {
        setTimeout(() => this.onload?.(), 0);
      }
    },
  );
});

// テスト完了後にモックを解除
afterAll(() => vi.unstubAllGlobals());

describe("useUploadMediaState", () => {
  it("Hook-14: サイズ・枚数制限内の画像を追加するとファイルが一覧に追加されること", async () => {
    // リクエストデータの作成
    const validFile = new File(["test"], "image.png", { type: "image/png" });

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useUploadMediaState());

    // 画像ファイルを追加
    await act(async () => result.current.setImageAndUrl([validFile]));

    // 追加されたファイルが存在することを確認
    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0].file.name).toBe("image.png");
  });

  it("Hook-15: 30枚の上限を超える画像を追加すると上限分のみ追加され超過分はスキップされること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useUploadMediaState());

    // 31枚の画像ファイルを生成
    const files = Array.from(
      { length: 31 },
      (_, i) => new File(["test"], `test_${i}.png`, { type: "image/png" }),
    );

    // 生成した画像ファイルリストを追加
    await act(async () => result.current.setImageAndUrl(files));

    // 上限である30枚のみが追加されエラーメッセージが表示されることを確認
    expect(result.current.items).toHaveLength(30);
    expect(toast.error).toHaveBeenCalledWith("上限のため1枚はスキップされました");
  });

  it("Hook-16: 20MBのサイズ制限を超える画像を追加するとサイズ超過ファイルが除外されること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useUploadMediaState());

    // Object.definePropertyを用いてファイルサイズを書き換え21MBのファイルを作成
    const overSizeFile = new File(["test"], "large.png", { type: "image/png" });
    Object.defineProperty(overSizeFile, "size", { value: 21 * 1024 * 1024 });

    // 生成した画像ファイルを追加
    await act(async () => result.current.setImageAndUrl([overSizeFile]));

    // 超過ファイルが一覧に追加されないことを確認
    expect(result.current.items).toHaveLength(0);
    expect(toast.error).toHaveBeenCalledWith("20MBを超える画像はスキップされました");
  });

  it("Hook-17: 特定インデックスのファイルを個別削除すると対象ファイルのみ一覧から除外されること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useUploadMediaState());

    // 2つの画像ファイルを作成
    const file1 = new File(["1"], "file1.png", { type: "image/png" });
    const file2 = new File(["2"], "file2.png", { type: "image/png" });

    // file1とfile2を追加
    await act(async () => result.current.setImageAndUrl([file1, file2]));

    // ファイル一覧が2件追加されていることを確認
    expect(result.current.items).toHaveLength(2);

    // file2を削除
    act(() => result.current.removeFile(1));

    // file1のみが残っていることを確認
    expect(result.current.items).toHaveLength(1);
    expect(result.current.items[0].file.name).toBe("file1.png");
    expect(URL.revokeObjectURL).toHaveBeenCalled();
  });

  it("Hook-18: 全ファイルを一括削除すると一覧が空になること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useUploadMediaState());

    // 2つの画像ファイルを作成
    const file1 = new File(["1"], "file1.png", { type: "image/png" });
    const file2 = new File(["2"], "file2.png", { type: "image/png" });

    // file1とfile2を追加
    await act(async () => result.current.setImageAndUrl([file1, file2]));

    // ファイル一覧が2件追加されていることを確認
    expect(result.current.items).toHaveLength(2);

    // 全ファイルを削除
    act(() => result.current.removeAllFiles());

    // ファイル一覧が空になることを確認
    expect(result.current.items).toHaveLength(0);
    expect(URL.revokeObjectURL).toHaveBeenCalledTimes(2);
  });
});
