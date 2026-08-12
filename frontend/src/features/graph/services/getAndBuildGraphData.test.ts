import { beforeEach, beforeAll, describe, expect, it, vi } from "vitest";
import { afterAll } from "vitest";

import { getCareRecords, getGrowthRecords } from "@/features/care/server";
import { getWordRecords } from "@/features/record/server";
import {
  CareRecordListResponseDto,
  CareRecordResponseDtoRecordTypeEnum,
  GrowthRecordResponseDto,
  WordRecordResponseDto,
} from "@/lib/api-client/gen";

import { getAndBuildGraphData } from "./getAndBuildGraphData";

// モック定義
vi.mock("@/features/care/server", () => ({
  getCareRecords: vi.fn(),
  getGrowthRecords: vi.fn(),
}));

vi.mock("@/features/record/server", () => ({
  getWordRecords: vi.fn(),
}));

const mockCareRecords: CareRecordListResponseDto = {
  items: [
    {
      id: 1,
      recordedBy: 1,
      recordType: CareRecordResponseDtoRecordTypeEnum.Milk,
      recordedAt: new Date("2026-01-01"),
      milkDetail: {
        amountMl: 200,
        note: "",
      },
      createdAt: new Date(),
      updatedAt: new Date(),
    },
    {
      id: 2,
      recordedBy: 1,
      recordType: CareRecordResponseDtoRecordTypeEnum.Diaper,
      recordedAt: new Date("2026-01-01"),
      diaperDetail: {
        diaperType: "WET",
        note: "",
      },
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  ],
};

const mockGetGrowthRecords: GrowthRecordResponseDto[] = [
  {
    id: 1,
    measurementDate: new Date("2026-01-01"),
    height: 75.0,
    weight: 9.0,
    note: "",
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];

const mockGetWordRecords: WordRecordResponseDto[] = [
  {
    id: 1,
    title: "まま",
    recordedDate: new Date("2026-01-01"),
    comment: "初めて呼ばれた",
    media: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];

// テスト用にシステム時刻を2026-01-01 12:00:00に固定
beforeAll(() => {
  vi.useFakeTimers();
  vi.setSystemTime(new Date("2026-01-01T12:00:00"));
});

// 各テスト実行前にモックの呼び出し履歴・戻り値をクリア
beforeEach(() => {
  vi.clearAllMocks();
});

// テスト終了後にタイマーをシステム時刻に戻す
afterAll(() => {
  vi.useRealTimers();
});

describe("getAndBuildGraphData", () => {
  it("Graph-01: 管理者権限で正常にデータを取得・集計できること", async () => {
    // リクエストデータの作成
    const isAdmin = true;

    // モック戻り値のセット
    vi.mocked(getCareRecords).mockResolvedValue(mockCareRecords);
    vi.mocked(getGrowthRecords).mockResolvedValue(mockGetGrowthRecords);
    vi.mocked(getWordRecords).mockResolvedValue(mockGetWordRecords);

    // 対象の実行
    const result = await getAndBuildGraphData(isAdmin);

    // 各取得処理が呼ばれたことの検証
    expect(getCareRecords).toHaveBeenCalledTimes(1);
    expect(getGrowthRecords).toHaveBeenCalledTimes(1);
    expect(getWordRecords).toHaveBeenCalledTimes(1);

    // 返却データの構造の検証
    expect(result.heightData).toHaveLength(12);
    expect(result.weightData).toHaveLength(12);
    expect(result.milkData).toHaveLength(7);
    expect(result.diaperData).toHaveLength(7);
    expect(result.wordData).toHaveLength(12);

    expect(result.careRecords.items).toHaveLength(2);
    expect(result.growthRecords).toHaveLength(1);
    expect(result.wordRecords).toHaveLength(1);

    // 1年で集計するデータの検証
    expect(result.heightData.find((data) => data.month === "2026/1")?.value).toBe(75.0);
    expect(result.weightData.find((data) => data.month === "2026/1")?.value).toBe(9.0);
    expect(result.wordData.find((data) => data.month === "2026/1")?.value).toBe(1);

    // 1週間で集計するデータの検証
    expect(result.milkData.find((data) => data.day === "1/1")?.value).toBe(200);
    expect(result.diaperData.find((data) => data.day === "1/1")?.value).toBe(1);
    expect(result.diaperData.find((data) => data.day === "1/1")?.secondValue).toBe(0);

    // 元データの検証
    expect(result.growthRecords[0].height).toBe(75.0);
    expect(result.growthRecords[0].weight).toBe(9.0);
    expect(result.wordRecords[0].title).toBe("まま");
    expect(result.careRecords.items[0].recordType).toBe(CareRecordResponseDtoRecordTypeEnum.Milk);
  });

  it("Graph-02: 一般ユーザー権限でリクエストした際、育児記録が空のデータで返ること", async () => {
    // リクエストデータの作成
    const isAdmin = false;

    // モック戻り値のセット
    vi.mocked(getCareRecords).mockResolvedValue(mockCareRecords);
    vi.mocked(getGrowthRecords).mockResolvedValue(mockGetGrowthRecords);
    vi.mocked(getWordRecords).mockResolvedValue(mockGetWordRecords);

    // 対象の実行
    const result = await getAndBuildGraphData(isAdmin);

    // 結果が空であることを検証
    expect(getCareRecords).toHaveBeenCalledTimes(0);
    expect(result.careRecords.items).toHaveLength(0);
  });

  it("Graph-03: 記録データが存在しない場合、空のデータで返ること", async () => {
    // リクエストデータの作成
    const isAdmin = true;

    // モック戻り値のセット
    vi.mocked(getCareRecords).mockResolvedValue({ items: [] });
    vi.mocked(getGrowthRecords).mockResolvedValue([]);
    vi.mocked(getWordRecords).mockResolvedValue([]);

    // 対象の実行
    const result = await getAndBuildGraphData(isAdmin);

    // 各取得処理が呼ばれたことの検証
    expect(getCareRecords).toHaveBeenCalledTimes(1);
    expect(getGrowthRecords).toHaveBeenCalledTimes(1);
    expect(getWordRecords).toHaveBeenCalledTimes(1);

    // 返却データの構造の検証
    expect(result.heightData).toHaveLength(12);
    expect(result.weightData).toHaveLength(12);
    expect(result.milkData).toHaveLength(7);
    expect(result.diaperData).toHaveLength(7);
    expect(result.wordData).toHaveLength(12);

    expect(result.careRecords.items).toHaveLength(0);
    expect(result.growthRecords).toHaveLength(0);
    expect(result.wordRecords).toHaveLength(0);
  });

  it("Graph-04: 同一月に複数の身体測定記録が存在する場合に最新の測定記録が採用されること", async () => {
    // リクエストデータの作成
    const isAdmin = true;

    // モック戻り値のセット
    vi.mocked(getCareRecords).mockResolvedValue(mockCareRecords);
    vi.mocked(getGrowthRecords).mockResolvedValue([
      {
        id: 1,
        measurementDate: new Date("2026-01-01"),
        height: 75.0,
        weight: 9.0,
        note: "",
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      {
        id: 2,
        measurementDate: new Date("2026-01-02"),
        height: 76.0,
        weight: 10.0,
        note: "",
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    ]);
    vi.mocked(getWordRecords).mockResolvedValue(mockGetWordRecords);

    // 対象の実行
    const result = await getAndBuildGraphData(isAdmin);

    // 各取得処理が呼ばれたことの検証
    expect(getGrowthRecords).toHaveBeenCalledTimes(1);

    expect(result.growthRecords).toHaveLength(2);

    // データの検証
    expect(result.heightData.find((data) => data.month === "2026/1")?.value).toBe(76.0);
    expect(result.weightData.find((data) => data.month === "2026/1")?.value).toBe(10.0);
  });

  it("Graph-05: 発育標準範囲の月齢を対象とする場合に標準範囲が表示されること", async () => {
    // リクエストデータの作成
    const isAdmin = true;

    // モック戻り値のセット
    vi.mocked(getCareRecords).mockResolvedValue(mockCareRecords);
    vi.mocked(getGrowthRecords).mockResolvedValue(mockGetGrowthRecords);
    vi.mocked(getWordRecords).mockResolvedValue(mockGetWordRecords);

    // 対象の実行
    const result = await getAndBuildGraphData(isAdmin);

    // データの検証
    const outOfRangeHeight = result.heightData.find((data) => data.month === "2025/8");
    const outOfRangeWeight = result.weightData.find((data) => data.month === "2025/8");
    expect(outOfRangeHeight).toBeDefined();
    expect(outOfRangeWeight).toBeDefined();
    expect(outOfRangeHeight?.standardRange).toBeUndefined();
    expect(outOfRangeWeight?.standardRange).toBeUndefined();

    const inOfRangeHeight = result.heightData.find((data) => data.month === "2025/9");
    const inOfRangeWeight = result.weightData.find((data) => data.month === "2025/9");
    expect(inOfRangeHeight?.standardRange).toEqual([51.3, 58.9]);
    expect(inOfRangeWeight?.standardRange).toEqual([3.76, 5.8]);
  });
});
