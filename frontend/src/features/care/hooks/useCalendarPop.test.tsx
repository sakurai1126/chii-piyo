import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, renderHook } from "@testing-library/react";
import React from "react";
import { describe, expect, it, vi } from "vitest";

import { CareRecordResponseDtoRecordTypeEnum } from "@/lib/api-client/gen";

import { updateCareRecordAction } from "../actions/updateCareRecordAction";

import { useCalendarPop } from "./useCalendarPop";

// QueryClientProvider のラッパー生成
const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return function Wrapper({ children }: { children: React.ReactNode }) {
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
  };
};

// サーバーアクションのモック化
vi.mock("../actions/updateCareRecordAction", () => ({
  updateCareRecordAction: vi.fn().mockResolvedValue({ success: true }),
}));

vi.mock("../actions/updateGrowthRecordAction", () => ({
  updateGrowthRecordAction: vi.fn().mockResolvedValue({ success: true }),
}));

// モックデータ定義
const careParams = {
  state: {
    isPopOpen: true,
    top: 100,
    left: 100,
    record: {
      id: 1,
      recordedBy: 1,
      recordType: CareRecordResponseDtoRecordTypeEnum.Milk,
      recordedAt: new Date("2026-01-01T12:00:00"),
      milkDetail: {
        amountMl: 200,
        note: "ミルク飲んだ",
      },
      createdAt: new Date(),
      updatedAt: new Date(),
    },
    growthRecord: null,
  },
  popCloseAction: vi.fn(),
};

const growthParams = {
  state: {
    isPopOpen: true,
    top: 100,
    left: 100,
    record: null,
    growthRecord: {
      id: 1,
      measurementDate: new Date("2026-01-02T00:00:00"),
      height: 75.0,
      weight: 9.0,
      note: "順調",
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  },
  popCloseAction: vi.fn(),
};

describe("useCalendarPop", () => {
  it("Hook-05: 育児記録がセットされた状態で編集モードを開くと記録の日時・メモ・種別固有の値が入力初期値にセットされること", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendarPop(careParams), { wrapper: createWrapper() });

    // 編集モードを開く
    act(() => result.current.editModeOpen());

    // 結果の検証
    expect(result.current.isEditMode).toBe(true);
    expect(result.current.updateData.date).toBe("2026-01-01");
    expect(result.current.updateData.time).toBe("12:00");
    expect(result.current.updateData.note).toBe("ミルク飲んだ");
    expect(result.current.updateData.amountMl).toBe(200);
  });

  it("Hook-06: 成長記録がセットされた状態で編集モードを開くと記録の日付・身長・体重・メモが入力初期値にセットされること", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendarPop(growthParams), { wrapper: createWrapper() });

    // 編集モードを開く
    act(() => result.current.editModeOpen());

    expect(result.current.isEditMode).toBe(true);
    expect(result.current.updateData.date).toBe("2026-01-02");
    expect(result.current.updateData.height).toBe(75.0);
    expect(result.current.updateData.weight).toBe(9.0);
    expect(result.current.updateData.note).toBe("順調");
  });

  it("Hook-07: 育児記録のバリデーションに失敗する入力で保存操作を実行すると更新APIが呼ばれずエラーになること", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendarPop(careParams), { wrapper: createWrapper() });

    act(() => result.current.editModeOpen());

    // バリデーションエラーになる不正な入力値としてミルク量をマイナス値に変更
    act(() => {
      result.current.setUpdateData((prev) => ({
        ...prev,
        amountMl: -50,
      }));
    });

    // 保存処理を実行
    act(() => result.current.saveCareRecordAction());

    // 更新APIが呼び出されていないことを確認
    expect(updateCareRecordAction).not.toHaveBeenCalled();
  });
});
