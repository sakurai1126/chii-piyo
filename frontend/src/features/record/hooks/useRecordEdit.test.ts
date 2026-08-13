import { act, renderHook } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { toast } from "@/components/ui/Toast";

import { useRecordEdit } from "./useRecordEdit";

// Toast のモック化
vi.mock("@/components/ui/Toast", () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

describe("useRecordEdit", () => {
  it("Hook-08: 必須項目を入力した状態で確認操作を実行した際に確認画面へ遷移できること", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() =>
      useRecordEdit({ setIsMenuOpen: vi.fn(), variant: "newFirstRecord" }),
    );

    // 必須項目を入力
    act(() => {
      result.current.setData({
        title: "はじめての寝返り",
        recordedDate: "2026-01-01",
        comment: "上手にできた",
      });
    });

    // 確認操作を実行
    act(() => result.current.confirmOpen());

    // 確認モーダルの表示状態を確認
    expect(result.current.isSaveConfirmOpen).toBe(true);
  });

  it("Hook-09: 必須項目が未入力の状態で確認操作を実行した際にエラー状態となること", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() =>
      useRecordEdit({ setIsMenuOpen: vi.fn(), variant: "newFirstRecord" }),
    );

    // titleが未入力の場合のチェック
    act(() => {
      result.current.setData({
        title: "",
        recordedDate: "2026-01-01",
        comment: "",
      });
    });
    act(() => result.current.confirmOpen());
    expect(result.current.isSaveConfirmOpen).toBe(false);
    expect(toast.error).toHaveBeenCalledWith("記録内容を入力してください");

    // モックの呼び出し履歴をリセット
    vi.clearAllMocks();

    // recordedDateが未入力の場合のチェック
    act(() => {
      result.current.setData({
        title: "はじめての寝返り",
        recordedDate: "",
        comment: "",
      });
    });
    act(() => result.current.confirmOpen());
    expect(result.current.isSaveConfirmOpen).toBe(false);
    expect(toast.error).toHaveBeenCalledWith("日時を入力してください");
  });

  it("Hook-10: 編集をキャンセルした場合入力内容および選択済みメディアが初期状態にリセットされること", () => {
    const mockSetIsMenuOpen = vi.fn();
    const initialEditData = {
      title: "初期タイトル",
      recordedDate: "2026-01-01",
      comment: "初期コメント",
      media: [{ id: 1, url: "https://example.com/image.png" }],
    };

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() =>
      useRecordEdit({
        setIsMenuOpen: mockSetIsMenuOpen,
        initialEditData,
        variant: "editFirstRecord",
      }),
    );

    // データを一時的に変更し、確認画面を開く
    act(() => {
      result.current.setData({
        title: "変更後のタイトル",
        recordedDate: "2026-01-02",
        comment: "変更後のコメント",
      });
      result.current.setSelectedMediaData([{ id: 2, url: "https://example.com/new.png" }]);
    });
    act(() => result.current.confirmOpen());

    // キャンセル操作を実行
    act(() => result.current.cancelEdit());

    // メニューが閉じられ、モーダル状態・入力値・選択メディアが初期状態にリセットされること
    expect(mockSetIsMenuOpen).toHaveBeenCalledWith(false);
    expect(result.current.isSaveConfirmOpen).toBe(false);
    expect(result.current.data.title).toBe("初期タイトル");
    expect(result.current.selectedMediaData).toEqual([]);
  });
});
