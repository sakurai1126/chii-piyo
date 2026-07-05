import { getCareRecords } from "@/features/care/api/getCareRecords";
import { getGrowthRecords } from "@/features/care/api/getGrowthRecords";
import { getWordRecords } from "@/features/record/api/getWordRecords";
import { formatShortDate, formatShortMonth } from "@/utils/date";

import { growthStandardRanges } from "./growthStandardRanges";

export const getAndBuildGraphData = async () => {
  // 誕生日指定
  const birthday = new Date("2025-08-06");

  // 今日のまでの１週間分の指定
  const today = new Date();
  const careStartDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 6);
  const growthStartDate = new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());

  // 初期データを取得
  const [careRecords, growthRecords, wordRecords] = await Promise.all([
    getCareRecords({ startDate: careStartDate, endDate: today }),
    getGrowthRecords({ startDate: growthStartDate, endDate: today }),
    getWordRecords(),
  ]);

  // 身長/体重共通のデータ取得処理
  const getMonthlyGrowthData = (i: number, type: "height" | "weight") => {
    // 対象月の初日を取得してそこから年と月を取得
    const targetMonthDate = new Date(today.getFullYear(), today.getMonth() - (11 - i), 1);
    const targetYear = targetMonthDate.getFullYear();
    const targetMonth = targetMonthDate.getMonth();

    // フォーマット用の文字列作成 (YYYY-MM)
    const monthKey = formatShortMonth(
      `${targetYear}-${(targetMonth + 1).toString().padStart(2, "0")}`,
    );

    // 取得した成長データ(growthRecords)から対象月の記録を抽出
    const recordsInMonth = growthRecords.filter((record) => {
      // 記録日時を取得
      const recordDate = new Date(record.measurementDate);
      // 以下条件に合うものを取得
      return (
        // 記録年が目的の年と一致
        recordDate.getFullYear() === targetYear &&
        // 記録月が目的の月と一致
        recordDate.getMonth() === targetMonth &&
        // 対象のデータが存在している
        ((type === "height" && record.height != null) ||
          (type === "weight" && record.weight != null))
      );
    });

    // 月に複数件ある場合の対処のため登録日時順にソート
    const sortedRecords = [...recordsInMonth].sort((a, b) => {
      return new Date(b.measurementDate).getTime() - new Date(a.measurementDate).getTime();
    });

    // 配列内の一件目（対象月内の最新データ）を取得
    const latestRecord = sortedRecords.length > 0 ? sortedRecords[0] : null;

    // 対象月初日時点の適正範囲算出のため月齢の算出
    // 年差を12か月に変換してから誕生月分を引いて対象月を加算する
    const monthsOld =
      (targetMonthDate.getFullYear() - birthday.getFullYear()) * 12 -
      birthday.getMonth() +
      targetMonth;

    // 計算した月齢から該当するstandardRangeを取得
    const range = growthStandardRanges.find(
      (r) => monthsOld >= r.ageRangeMonths[0] && monthsOld < r.ageRangeMonths[1],
    );

    return { monthKey, latestRecord, range };
  };

  // 直近1年分の身長データの作成
  const heightData = Array.from({ length: 12 }, (_, i) => {
    const { monthKey, latestRecord, range } = getMonthlyGrowthData(i, "height");

    return {
      month: monthKey,
      standardRange: range ? range.heightRange : undefined,
      value: latestRecord?.height ?? null,
    };
  });

  // 直近1年分の体重データの作成
  const weightData = Array.from({ length: 12 }, (_, i) => {
    const { monthKey, latestRecord, range } = getMonthlyGrowthData(i, "weight");
    return {
      month: monthKey,
      standardRange: range ? range.weightRange : undefined,
      value: latestRecord?.weight ?? null,
    };
  });

  // ミルク量データ
  const milkData = Array.from({ length: 7 }, (_, i) => {
    // 対象の日付を取得
    const targetDate = formatShortDate(targetDay(today, i - 6));
    // careRecordsから対象日のミルク記録のみを抽出しamountMlを合計する
    const totalMilkAmount = careRecords.items
      .filter(
        (record) =>
          record.recordType === "MILK" && formatShortDate(record.recordedAt) === targetDate,
      )
      .reduce((sum, record) => sum + (record.milkDetail?.amountMl ?? 0), 0);

    return {
      day: targetDate,
      value: totalMilkAmount,
    };
  });

  // 排泄記録データ
  const diaperData = Array.from({ length: 7 }, (_, i) => {
    // 対象の日付を取得
    const targetDate = formatShortDate(targetDay(today, i - 6));
    // careRecordsから対象日の排泄記録のみを抽出しそれぞれ回数を合計する
    const wetCount = careRecords.items
      .filter(
        (record) =>
          record.recordType === "DIAPER" &&
          formatShortDate(record.recordedAt) === targetDate &&
          record.diaperDetail?.diaperType === "WET",
      )
      .reduce((sum) => sum + 1, 0);

    const dirtyCount = careRecords.items
      .filter(
        (record) =>
          record.recordType === "DIAPER" &&
          formatShortDate(record.recordedAt) === targetDate &&
          record.diaperDetail?.diaperType === "DIRTY",
      )
      .reduce((sum) => sum + 1, 0);

    return {
      day: targetDate,
      value: wetCount,
      secondValue: dirtyCount,
    };
  });

  return {
    heightData,
    weightData,
    milkData,
    diaperData,
    careRecords,
    growthRecords,
    wordRecords,
  };
};

const targetDay = (base: Date, days: number) => {
  return new Date(base.getFullYear(), base.getMonth(), base.getDate() + days);
};
