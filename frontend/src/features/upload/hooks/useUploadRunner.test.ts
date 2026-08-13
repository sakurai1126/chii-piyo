import { act, renderHook } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { createMediaAction } from "../actions/createMediaAction";
import { updateMediaStatusAction } from "../actions/updateMediaStatusAction";
import { uploadToS3 } from "../lib/uploadToS3";
import { UploadMedia } from "../types";

import { useUploadRunner } from "./useUploadRunner";

// 各処理のモック化
vi.mock("../actions/createMediaAction", () => ({
  createMediaAction: vi.fn(),
}));

vi.mock("../actions/updateMediaStatusAction", () => ({
  updateMediaStatusAction: vi.fn(),
}));

vi.mock("../lib/uploadToS3", () => ({
  uploadToS3: vi.fn(),
}));

// テストごとにモックをリセット
beforeEach(() => vi.clearAllMocks());

// アップロードファイルモック作成ヘルパー
const createMockItem = (id: string, name = "image.png"): UploadMedia => ({
  id,
  file: new File(["dummy content"], name, { type: "image/png" }),
  previewUrl: "blob:http://localhost/dummy",
  status: "idle",
  progress: 0,
  width: 100,
  height: 100,
  metadata: {
    takenAt: "2026-01-01",
    albumId: undefined,
    sharingGroupId: undefined,
    tagIds: [],
    comment: "",
  },
});

const mockCreateMediaActionSuccessResult = {
  success: true as const,
  data: { mediaId: 1, presignedUrl: "https://example.com/upload" },
};

describe("useUploadRunner", () => {
  it("Hook-19: 複数ファイルのアップロード処理を開始すると最大3並列で処理が実行され、全件完了後に完了コールバックが呼ばれること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const onAllComplete = vi.fn();
    const { result } = renderHook(() => useUploadRunner({ onAllComplete }));

    // 各送信処理をモック化
    vi.mocked(createMediaAction).mockResolvedValue(mockCreateMediaActionSuccessResult);
    vi.mocked(updateMediaStatusAction).mockResolvedValue({ success: true });

    // 現在実行中の通信数
    let activeCount = 0;
    // 同時に走った通信数の最大値
    let maxActiveCount = 0;
    vi.mocked(uploadToS3).mockImplementation(async () => {
      activeCount++;
      // 最大値を記録
      maxActiveCount = Math.max(maxActiveCount, activeCount);
      // 短時間の非同期待ち時間を作り、同時実行状態を再現
      await new Promise((resolve) => setTimeout(resolve, 50));
      activeCount--;
    });

    // テスト用のアップロードファイルを用意
    const items = [
      createMockItem("1"),
      createMockItem("2"),
      createMockItem("3"),
      createMockItem("4"),
    ];

    // 全件アップロード実行
    await act(async () => result.current.upload(items));

    // 最大並列数が3件であることを確認
    expect(maxActiveCount).toBe(3);
    // 全件完了コールバックが成功件数4、失敗件数0で呼ばれていることを確認
    expect(onAllComplete).toHaveBeenCalledWith({ successCount: 4, failedCount: 0 });
    // アップロードが完了していることを確認
    expect(result.current.isUploading).toBe(false);
  });

  it("Hook-20: アップロード処理中に通信エラーが発生すると失敗件数がカウントされエラー情報が保持されること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const onAllComplete = vi.fn();
    const onItemUpdate = vi.fn();
    const { result } = renderHook(() => useUploadRunner({ onItemUpdate, onAllComplete }));

    // 各送信処理をモック化
    vi.mocked(uploadToS3).mockResolvedValue();
    vi.mocked(updateMediaStatusAction).mockResolvedValue({ success: true });
    // メディア作成API送信にて1件目は成功、2件目はエラーを発生させる
    vi.mocked(createMediaAction)
      .mockResolvedValueOnce(mockCreateMediaActionSuccessResult)
      .mockResolvedValueOnce({ success: false, error: "通信エラーが発生しました" });

    // テスト用のアップロードファイルを用意
    const items = [createMockItem("1"), createMockItem("2")];

    // ファイルアップロード実行
    await act(async () => result.current.upload(items));

    // 成功1件、失敗1件でカウントされて完了することを確認
    expect(onAllComplete).toHaveBeenCalledWith({ successCount: 1, failedCount: 1 });

    // 失敗した2件目のステータス変更通知にエラーメッセージが含まれていることを確認
    expect(onItemUpdate).toHaveBeenCalledWith(
      "2",
      expect.objectContaining({
        status: "failed",
        errorMessage: "通信エラーが発生しました",
      }),
    );
  });

  it("Hook-21: 既にアップロード中に再度実行を呼び出すと二重実行されずスキップされること", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const onAllComplete = vi.fn();
    const { result } = renderHook(() => useUploadRunner({ onAllComplete }));

    // 各送信処理をモック化
    vi.mocked(createMediaAction).mockResolvedValue(mockCreateMediaActionSuccessResult);
    vi.mocked(updateMediaStatusAction).mockResolvedValue({ success: true });

    // uploadToS3 に遅延を入れてアップロード中の状態を作成
    vi.mocked(uploadToS3).mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 100)),
    );

    // テスト用のアップロードファイルを用意
    const firstItems = [createMockItem("1", "first.png")];
    const secondItems = [createMockItem("2", "second.png")];

    // 二重実行開始状況を再現するため、awaitなしで最初のアップロードを実行する
    let firstUploadPromise: Promise<void>;
    act(() => {
      firstUploadPromise = result.current.upload(firstItems);
    });

    // アップロード中のステータスになっていること
    expect(result.current.isUploading).toBe(true);

    // アップロード中にもう一度呼び出す
    await act(async () => result.current.upload(secondItems));

    // 最初のアップロードの完了を待つ
    await act(async () => firstUploadPromise);

    // スキップされ完了コールバックが1回しか呼ばれていないこと
    expect(onAllComplete).toHaveBeenCalledTimes(1);

    // 1回目のファイルのみ処理され、2回目のファイルは処理されていないことを検証
    expect(createMediaAction).toHaveBeenCalledWith(
      expect.objectContaining({ originalFilename: "first.png" }),
    );
    expect(createMediaAction).not.toHaveBeenCalledWith(
      expect.objectContaining({ originalFilename: "second.png" }),
    );
  });

  it("Hook-22: アップロード中に処理を中断すると処理が中断され残キューが実行されないこと", async () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const onAllComplete = vi.fn();
    const { result, unmount } = renderHook(() => useUploadRunner({ onAllComplete }));

    // 各送信処理をモック化
    vi.mocked(createMediaAction).mockResolvedValue(mockCreateMediaActionSuccessResult);
    // モックに遅延を入れて、3件の処理中にアンマウントできるようにする
    vi.mocked(uploadToS3).mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 100)),
    );
    vi.mocked(updateMediaStatusAction).mockResolvedValue({ success: true });

    // テスト用のアップロードファイルを用意
    const items = [
      createMockItem("1"),
      createMockItem("2"),
      createMockItem("3"),
      createMockItem("4"),
    ];

    // 3件時点の中断処理を確認するためawaitなしでアップロードを開始する
    let uploadPromise: Promise<void>;
    act(() => {
      uploadPromise = result.current.upload(items);
    });

    // 3件目までが並列で処理開始されていることを確認
    expect(createMediaAction).toHaveBeenCalledTimes(3);

    // アンマウントして中断発生
    act(() => unmount());

    // 未完了のPromiseを回収
    await act(async () => uploadPromise);

    // 残キューの4件目が追加で実行されていないことを確認
    expect(createMediaAction).toHaveBeenCalledTimes(3);
  });
});
