import { describe, expect, it, vi } from "vitest";

import { toast } from "@/components/ui/Toast";
import { CareRecordResponseDto } from "@/lib/api-client/gen";

import { UpdateDataParams } from "../types";

import { validateCareRecordUpdate, validateGrowthRecordUpdate } from "./validation";

// モック定義
vi.mock("@/components/ui/Toast", () => ({
  toast: { error: vi.fn() },
}));

describe("validateMilkUpdate", () => {
  // 共通リクエストデータの作成
  const careRecords = { recordType: "MILK" } as CareRecordResponseDto;

  // アップデートリクエスト作成ヘルパー
  const createUpdateData = (amountMl: number | undefined): UpdateDataParams => {
    return {
      date: "2026-01-01",
      time: "12:00",
      note: "",
      amountMl,
      diaperType: undefined,
      temperature: undefined,
      height: undefined,
      weight: undefined,
    };
  };

  it("Val-01: ミルク量に有効値を渡した場合、trueが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(200);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });

  it("Val-02: ミルク量が未入力の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(undefined);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("ミルク量を入力してください。");
  });

  it("Val-03: ミルク量が400mlを超える場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(401);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("ミルク量を400ml以内で入力してください。");
  });

  it("Val-04: ミルク量が10ml未満の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(9);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("ミルク量を10ml以上で入力してください。");
  });

  it("Val-05: ミルク量が10ml / 400mlの場合、trueが返ること", () => {
    // リクエストデータの作成
    const minUpdateData = createUpdateData(10);
    const maxUpdateData = createUpdateData(400);

    // 対象を実行
    const minResult = validateCareRecordUpdate(careRecords, minUpdateData);
    const maxResult = validateCareRecordUpdate(careRecords, maxUpdateData);

    // 結果の検証
    expect(minResult).toBe(true);
    expect(maxResult).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });
});

describe("validateDiaperUpdate", () => {
  // 共通リクエストデータの作成
  const careRecords = { recordType: "DIAPER" } as CareRecordResponseDto;

  // アップデートリクエスト作成ヘルパー
  const createUpdateData = (diaperType: string | undefined): UpdateDataParams => {
    return {
      date: "2026-01-01",
      time: "12:00",
      note: "",
      amountMl: undefined,
      diaperType,
      temperature: undefined,
      height: undefined,
      weight: undefined,
    };
  };

  it("Val-06: 排泄タイプに有効値を渡した場合、trueが返ること", () => {
    // リクエストデータの作成
    const dirtyUpdateData = createUpdateData("DIRTY");
    const wetUpdateData = createUpdateData("WET");

    // 対象を実行
    const dirtyResult = validateCareRecordUpdate(careRecords, dirtyUpdateData);
    const wetResult = validateCareRecordUpdate(careRecords, wetUpdateData);

    // 結果の検証
    expect(dirtyResult).toBe(true);
    expect(wetResult).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });

  it("Val-07: 排泄タイプが未入力の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(undefined);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("排泄タイプを入力してください。");
  });

  it("Val-08: 排泄タイプが不正な値を入力した場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData("INVALID");

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("排泄タイプが不正です。");
  });
});

describe("validateHealthUpdate", () => {
  // 共通リクエストデータの作成
  const careRecords = { recordType: "HEALTH" } as CareRecordResponseDto;

  // アップデートリクエスト作成ヘルパー
  const createUpdateData = (temperature: number | undefined): UpdateDataParams => {
    return {
      date: "2026-01-01",
      time: "12:00",
      note: "",
      amountMl: undefined,
      diaperType: undefined,
      temperature,
      height: undefined,
      weight: undefined,
    };
  };

  it("Val-09: 体温に有効値を渡した場合、trueが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(36.5);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });

  it("Val-10: 体温が未入力の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData(undefined);

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("体温を入力してください。");
  });

  it("Val-11: 体温が34度未満 / 42度超の場合、falseが返ること", () => {
    // リクエストデータの作成
    const minUpdateData = createUpdateData(33.9);
    const maxUpdateData = createUpdateData(42.1);

    // 対象を実行
    const minResult = validateCareRecordUpdate(careRecords, minUpdateData);
    const maxResult = validateCareRecordUpdate(careRecords, maxUpdateData);

    // 結果の検証
    expect(minResult).toBe(false);
    expect(maxResult).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledTimes(2);
    expect(toast.error).toHaveBeenCalledWith("体温を正しく入力してください");
  });

  it("Val-12: 体温が34度 / 42度の場合、trueが返ること", () => {
    // リクエストデータの作成
    const minUpdateData = createUpdateData(34.0);
    const maxUpdateData = createUpdateData(42.0);

    // 対象を実行
    const minResult = validateCareRecordUpdate(careRecords, minUpdateData);
    const maxResult = validateCareRecordUpdate(careRecords, maxUpdateData);

    // 結果の検証
    expect(minResult).toBe(true);
    expect(maxResult).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });
});

describe("validateGrowthRecordUpdate", () => {
  // アップデートリクエスト作成ヘルパー
  const createUpdateData = ({
    height,
    weight,
  }: {
    height: number | undefined;
    weight: number | undefined;
  }): UpdateDataParams => {
    return {
      date: "2026-01-01",
      time: "12:00",
      note: "",
      amountMl: undefined,
      diaperType: undefined,
      temperature: undefined,
      height,
      weight,
    };
  };

  it("Val-13: 身長または体重に有効値を渡した場合、trueが返ること", () => {
    // リクエストデータの作成
    const updateDataAll = createUpdateData({ height: 100, weight: 15 });
    const updateDataHeight = createUpdateData({ height: 100, weight: undefined });
    const updateDataWeight = createUpdateData({ height: undefined, weight: 15 });

    // 対象を実行
    const allResult = validateGrowthRecordUpdate(updateDataAll);
    const heightResult = validateGrowthRecordUpdate(updateDataHeight);
    const weightResult = validateGrowthRecordUpdate(updateDataWeight);

    // 結果の検証
    expect(allResult).toBe(true);
    expect(heightResult).toBe(true);
    expect(weightResult).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });

  it("Val-14: 身長・体重どちらも未入力の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateData = createUpdateData({ height: undefined, weight: undefined });

    // 対象を実行
    const result = validateGrowthRecordUpdate(updateData);

    // 結果の検証
    expect(result).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledWith("身長または体重を入力してください");
  });

  it("Val-15: 身長が0以下 / 200超の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateDataUnder = createUpdateData({ height: 0, weight: undefined });
    const updateDataOver = createUpdateData({ height: 201, weight: undefined });

    // 対象を実行
    const resultUnder = validateGrowthRecordUpdate(updateDataUnder);
    const resultOver = validateGrowthRecordUpdate(updateDataOver);

    // 結果の検証
    expect(resultUnder).toBe(false);
    expect(resultOver).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledTimes(2);
    expect(toast.error).toHaveBeenCalledWith("身長を正しく入力してください");
  });

  it("Val-16: 体重が0以下 / 200超の場合、falseが返ること", () => {
    // リクエストデータの作成
    const updateDataUnder = createUpdateData({ height: undefined, weight: 0 });
    const updateDataOver = createUpdateData({ height: undefined, weight: 201 });

    // 対象を実行
    const resultUnder = validateGrowthRecordUpdate(updateDataUnder);
    const resultOver = validateGrowthRecordUpdate(updateDataOver);

    // 結果の検証
    expect(resultUnder).toBe(false);
    expect(resultOver).toBe(false);

    // トーストが呼び出されていることを検証
    expect(toast.error).toHaveBeenCalledTimes(2);
    expect(toast.error).toHaveBeenCalledWith("体重を正しく入力してください");
  });
});

describe("validateCareRecordUpdate", () => {
  it("Val-17: 食事記録の際はtrueが返ること", () => {
    // リクエストデータの作成
    const careRecords = { recordType: "MEAL" } as CareRecordResponseDto;
    const updateData: UpdateDataParams = {
      date: "2026-01-01",
      time: "12:00",
      note: "",
      amountMl: undefined,
      diaperType: undefined,
      temperature: undefined,
      height: undefined,
      weight: undefined,
    };

    // 対象を実行
    const result = validateCareRecordUpdate(careRecords, updateData);

    // 結果の検証
    expect(result).toBe(true);

    // トーストが呼び出されていないことを検証
    expect(toast.error).not.toHaveBeenCalled();
  });
});
