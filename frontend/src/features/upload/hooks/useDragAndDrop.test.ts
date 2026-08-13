import { act, renderHook } from "@testing-library/react";
import React from "react";
import { describe, expect, it, vi } from "vitest";

import { useDragAndDrop } from "./useDragAndDrop";

// DragEventのモック生成ヘルパー
const createMockDragEvent = (files: File[]) => {
  return {
    preventDefault: vi.fn(),
    dataTransfer: {
      files,
    },
  } as unknown as React.DragEvent<HTMLElement>;
};

describe("useDragAndDrop", () => {
  it("Hook-11: 指定形式のファイルをドロップするとファイル検証を通過しファイル追加処理が実行されること", () => {
    // リクエストデータの作成
    const mockOnFilesAdd = vi.fn();
    const validFile = new File(["test"], "image.png", { type: "image/png" });
    const mockEvent = createMockDragEvent([validFile]);

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() =>
      useDragAndDrop({ onFilesAdd: mockOnFilesAdd, acceptTypes: "image" }),
    );

    // ドロップ操作
    act(() => result.current.handleDrop(mockEvent));

    // ファイル追加処理が対象ファイルで呼び出されていることを確認
    expect(mockOnFilesAdd).toHaveBeenCalledWith([validFile]);
  });

  it("Hook-12: 対象外形式のファイルをドロップすると対象外ファイルが除外され追加処理が実行されないこと", () => {
    // リクエストデータの作成
    const mockOnFilesAdd = vi.fn();
    const invalidFile = new File(["test"], "document.pdf", { type: "application/pdf" });
    const mockInvalidEvent = createMockDragEvent([invalidFile]);

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() =>
      useDragAndDrop({ onFilesAdd: mockOnFilesAdd, acceptTypes: "image" }),
    );

    // 対象外のPDFファイルをドロップするイベントを発生
    act(() => result.current.handleDrop(mockInvalidEvent));

    // ファイル追加処理が呼び出されないことを確認
    expect(mockOnFilesAdd).not.toHaveBeenCalled();
  });

  it("Hook-13: 子要素をまたいでドラッグした場合ドラッグ状態が途中で意図せず解除されないこと", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() =>
      useDragAndDrop({ onFilesAdd: vi.fn(), acceptTypes: "image" }),
    );

    // 親要素に進入
    act(() => result.current.handleDragEnter());
    expect(result.current.isDragging).toBe(true);

    // 子要素に進入
    act(() => result.current.handleDragEnter());
    expect(result.current.isDragging).toBe(true);

    // 親要素側を離れ子要素に移動
    act(() => result.current.handleDragLeave());
    expect(result.current.isDragging).toBe(true);

    // エリア外へ離脱
    act(() => result.current.handleDragLeave());
    expect(result.current.isDragging).toBe(false);
  });
});
