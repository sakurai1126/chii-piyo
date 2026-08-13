import { beforeEach, describe, expect, it, vi } from "vitest";

import { uploadToS3 } from "./uploadToS3";

// イベントハンドラを保持するオブジェクト
let listeners: Record<string, () => void> = {};
const createMockXhr = (status: number = 200, listener: string = "load") => {
  return {
    open: vi.fn(),
    // 非同期処理化して同期処理が完了したら完了イベントを発火
    send: vi.fn().mockImplementation(() => queueMicrotask(() => listeners[listener]?.())),
    abort: vi.fn().mockImplementation(() => listeners["abort"]?.()),
    upload: {
      addEventListener: vi.fn(),
    },
    addEventListener: vi.fn((event: string, callback: () => void) => {
      listeners[event] = callback;
    }),
    setRequestHeader: vi.fn(),
    status,
  };
};

// リクエストデータの作成
const presignedUrl = "https://example.com/presigned-url";
const file = new File(["test"], "image.png", { type: "image/png" });

// テスト前にイベントハンドラを初期化
beforeEach(() => {
  listeners = {};
});

describe("uploadToS3", () => {
  it("Upload-01: アップロードURLとファイルを渡して実行するとアップロードが実行されること", async () => {
    // XMLHttpRequestのモック化
    const mockXhr = createMockXhr();

    vi.spyOn(globalThis, "XMLHttpRequest").mockImplementation(function () {
      return mockXhr;
    });

    // 対象の実行
    await uploadToS3({ presignedUrl, file });

    // XMLHttpRequestが開かれたかの確認
    expect(mockXhr.open).toHaveBeenCalledWith("PUT", presignedUrl);

    // Content-Typeヘッダーが設定されたかの確認
    expect(mockXhr.setRequestHeader).toHaveBeenCalledWith("Content-Type", file.type);

    // ファイルが送信されたかの確認
    expect(mockXhr.send).toHaveBeenCalledWith(file);
  });

  it("Upload-02: S3から4xx/5xxレスポンスの場合、適切なエラーが送出されること", async () => {
    // XMLHttpRequestのモック化
    const mockXhr = createMockXhr(400);
    vi.spyOn(globalThis, "XMLHttpRequest").mockImplementation(function () {
      return mockXhr;
    });

    // 対象の実行
    const uploadPromise = uploadToS3({ presignedUrl, file });
    await expect(uploadPromise).rejects.toThrow("S3アップロード失敗: HTTP 400");
  });

  it("Upload-03: ネットワークエラーが発生した場合、適切なエラーが送出されること", async () => {
    // XMLHttpRequestのモック化
    const mockXhr = createMockXhr(400, "error");
    vi.spyOn(globalThis, "XMLHttpRequest").mockImplementation(function () {
      return mockXhr;
    });

    // 対象の実行
    const uploadPromise = uploadToS3({ presignedUrl, file });
    await expect(uploadPromise).rejects.toThrow(
      "S3アップロード中にネットワークエラーが発生しました",
    );
  });

  it("Upload-04: アップロード途中でによる中断が発生した場合、処理が中断され、中断エラーが送出されること", async () => {
    // XMLHttpRequestのモック化
    const mockXhr = createMockXhr(400);
    vi.spyOn(globalThis, "XMLHttpRequest").mockImplementation(function () {
      return mockXhr;
    });

    // アップロードを開始直後に中断を実行
    const controller = new AbortController();
    const uploadPromise = uploadToS3({ presignedUrl, file, signal: controller.signal });
    controller.abort();

    // 中断処理が呼ばれていることの確認
    await expect(uploadPromise).rejects.toThrow("S3アップロードが中断されました");
    expect(mockXhr.abort).toHaveBeenCalled();
  });
});
