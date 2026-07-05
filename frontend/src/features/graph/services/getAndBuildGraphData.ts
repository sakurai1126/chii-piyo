import { getCareRecords } from "@/features/care/api/getCareRecords";
import { getGrowthRecords } from "@/features/care/api/getGrowthRecords";
import { getWordRecords } from "@/features/record/api/getWordRecords";
import { formatJapaneseDateNonTime, formatShortDate } from "@/utils/date";

export const getAndBuildGraphData = async () => {
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

  // モック定義
  const heightData = [
    { month: formatJapaneseDateNonTime("2026-05-01"), standardRange: [44.0, 52.6], value: 48.0 },
    { month: formatJapaneseDateNonTime("2026-06-01"), standardRange: [50.0, 58.4], value: 53.5 },
    { month: formatJapaneseDateNonTime("2026-07-01"), standardRange: [53.3, 61.7], value: 58.0 },
    { month: formatJapaneseDateNonTime("2026-08-01"), standardRange: [55.9, 64.5], value: 58.0 },
  ];

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
