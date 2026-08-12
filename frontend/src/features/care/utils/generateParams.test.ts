import { describe, expect, it } from "vitest";

import { CareRecordResponseDto } from "@/lib/api-client/gen";

import { UpdateDataParams } from "../types";

import { generateUpdateCareRecordActionParams } from "./generateParams";

const updateData: UpdateDataParams = {
  date: "2026-01-01",
  time: "12:00",
  note: "メモ",
  amountMl: 200,
  diaperType: "DIRTY",
  temperature: 36.5,
  height: undefined,
  weight: undefined,
};

describe("generateUpdateCareRecordActionParams", () => {
  it("Param-01: 食事記録を渡した場合、食事記録のみが作成されること", () => {
    // リクエストデータの作成
    const record = {
      id: 1,
      recordType: "MEAL",
    } as CareRecordResponseDto;

    // 対象の実行
    const result = generateUpdateCareRecordActionParams(record, updateData);

    // 検証
    expect(result).toEqual({
      id: 1,
      recordType: "MEAL",
      recordedAt: new Date("2026-01-01T12:00"),
      mealDetail: {
        note: "メモ",
      },
      milkDetail: undefined,
      diaperDetail: undefined,
      healthDetail: undefined,
    });
  });

  it("Param-02: ミルク記録を渡した場合、ミルク記録のみが作成されること", () => {
    // リクエストデータの作成
    const record = {
      id: 1,
      recordType: "MILK",
    } as CareRecordResponseDto;

    // 対象の実行
    const result = generateUpdateCareRecordActionParams(record, updateData);

    // 検証
    expect(result).toEqual({
      id: 1,
      recordType: "MILK",
      recordedAt: new Date("2026-01-01T12:00"),
      mealDetail: undefined,
      milkDetail: {
        amountMl: 200,
        note: "メモ",
      },
      diaperDetail: undefined,
      healthDetail: undefined,
    });
  });

  it("Param-03: 排泄記録を渡した場合、排泄記録のみが作成されること", () => {
    // リクエストデータの作成
    const record = {
      id: 1,
      recordType: "DIAPER",
    } as CareRecordResponseDto;

    // 対象の実行
    const result = generateUpdateCareRecordActionParams(record, updateData);

    // 検証
    expect(result).toEqual({
      id: 1,
      recordType: "DIAPER",
      recordedAt: new Date("2026-01-01T12:00"),
      mealDetail: undefined,
      milkDetail: undefined,
      diaperDetail: {
        diaperType: "DIRTY",
        note: "メモ",
      },
      healthDetail: undefined,
    });
  });

  it("Param-04: 体調記録を渡した場合、体調記録のみが作成されること", () => {
    // リクエストデータの作成
    const record = {
      id: 1,
      recordType: "HEALTH",
    } as CareRecordResponseDto;

    // 対象の実行
    const result = generateUpdateCareRecordActionParams(record, updateData);

    // 検証
    expect(result).toEqual({
      id: 1,
      recordType: "HEALTH",
      recordedAt: new Date("2026-01-01T12:00"),
      mealDetail: undefined,
      milkDetail: undefined,
      diaperDetail: undefined,
      healthDetail: {
        temperature: 36.5,
        note: "メモ",
      },
    });
  });

  it("Param-05: 種別に対応する値がundefinedの場合、該当detailがundefinedになること", () => {
    // リクエストデータの作成
    const milkRecord = {
      id: 1,
      recordType: "MILK",
    } as CareRecordResponseDto;
    const diaperRecord = {
      id: 1,
      recordType: "DIAPER",
    } as CareRecordResponseDto;
    const healthRecord = {
      id: 1,
      recordType: "HEALTH",
    } as CareRecordResponseDto;

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

    // 対象の実行
    const milkResult = generateUpdateCareRecordActionParams(milkRecord, updateData);
    const diaperResult = generateUpdateCareRecordActionParams(diaperRecord, updateData);
    const healthResult = generateUpdateCareRecordActionParams(healthRecord, updateData);

    // 検証
    expect(milkResult.milkDetail).toBeUndefined();
    expect(diaperResult.diaperDetail).toBeUndefined();
    expect(healthResult.healthDetail).toBeUndefined();
  });
});
