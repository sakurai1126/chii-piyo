import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { act, renderHook } from "@testing-library/react";
import React from "react";
import { afterAll, beforeAll, describe, expect, it, vi } from "vitest";

import { CareRecordListResponseDto, GrowthRecordResponseDto } from "@/lib/api-client/gen";

import { useCalendar } from "./useCalendar";

// useQueryClientを利用するためQueryClientProviderのラッパー生成
const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        // デフォルトでは通信エラーが起きると自動で3回リトライするため無効化
        retry: false,
      },
    },
  });

  const Wrapper = ({ children }: { children: React.ReactNode }) => {
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
  };

  return Wrapper;
};

// 取得系依存をモック化
vi.mock("./useGetCareRecords", () => ({
  useGetCareRecords: ({ initialData }: { initialData?: CareRecordListResponseDto }) => ({
    data: initialData ?? { items: [] },
  }),
}));

vi.mock("./useGetGrowthRecords", () => ({
  useGetGrowthRecords: ({ initialData }: { initialData?: GrowthRecordResponseDto[] }) => ({
    data: initialData ?? [],
  }),
}));

const params: {
  initialCareRecords: CareRecordListResponseDto;
  initialGrowthRecords: GrowthRecordResponseDto[];
} = {
  initialCareRecords: {
    items: [
      {
        id: 1,
        recordedBy: 1,
        recordType: "MILK",
        recordedAt: new Date("2026-01-01"),
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    ],
  },

  initialGrowthRecords: [
    {
      id: 1,
      measurementDate: new Date("2026-01-01"),
      height: 75.0,
      weight: 9.0,
      note: "",
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  ],
};

// 日付計算テストのため基準日時を 2026-01-01 12:00:00 (木曜日) に固定
beforeAll(() => {
  vi.useFakeTimers();
  vi.setSystemTime(new Date("2026-01-01T12:00:00"));
});

// テスト後にモック化した日時を戻す
afterAll(() => {
  vi.useRealTimers();
});

describe("useCalendar", () => {
  it("Hook-01: 初期化時当週の日付一覧および当日の選択状態が生成されること", () => {
    // 確認用の基準日から直近の日曜日
    const targetDay = new Date("2025-12-28T00:00:00");

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendar(params), { wrapper: createWrapper() });

    // 結果の検証
    expect(result.current.weeklyDates).toHaveLength(7);
    expect(result.current.weeklyDates[0].toDateString()).toBe(targetDay.toDateString());
    expect(result.current.isTodayWeek).toBe(true);
    expect(result.current.currentDay.toDateString()).toBe(new Date("2026-01-01").toDateString());

    // 渡したデータがそのまま届いているかを検証
    expect(result.current.careRecords).toEqual(params.initialCareRecords);
    expect(result.current.growthRecords).toEqual(params.initialGrowthRecords);
  });

  it("Hook-02: 翌週への移動操作を実行すると表示週が7日後にずれて週の日付一覧が再生成されること", () => {
    // 基準日翌週の日曜日
    const targetDay = new Date("2026-01-04T00:00:00");

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendar(params), { wrapper: createWrapper() });

    // 週変更処理を実行
    act(() => result.current.changeWeek(7));

    expect(result.current.weeklyDates[0].toDateString()).toBe(targetDay.toDateString());
    expect(result.current.isTodayWeek).toBe(false);
  });

  it("Hook-03: 日付移動で週をまたぐ場合自動的に週変更が実行され表示週が更新されること", () => {
    // 基準日翌週の日曜日
    const targetDay = new Date("2026-01-04T12:00:00");

    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendar(params), { wrapper: createWrapper() });

    // 日曜日までの日時変更処理を実行
    act(() => result.current.changeDays(3));

    // 表示対象日時が変更されていることを確認
    expect(result.current.currentDay.toDateString()).toBe(targetDay.toDateString());

    // 翌週となる対象日時から週が始まっているかを確認
    expect(result.current.weeklyDates[0].toDateString()).toBe(targetDay.toDateString());
  });

  it("Hook-04: 当週以外に移動後、当週に戻った場合現在週のフラグが切り替わること", () => {
    // 仮想コンポーネントを用意しカスタムフックを実行
    const { result } = renderHook(() => useCalendar(params), { wrapper: createWrapper() });

    // 翌週へ移動し当週フラグを検証
    act(() => result.current.changeWeek(7));
    expect(result.current.isTodayWeek).toBe(false);

    // 当週へ移動し当週フラグを検証
    act(() => result.current.changeWeek(-7));
    expect(result.current.isTodayWeek).toBe(true);
  });
});
