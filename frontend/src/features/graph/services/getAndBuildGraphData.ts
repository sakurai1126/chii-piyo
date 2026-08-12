import "server-only";

import { getGrowthRecords, getCareRecords } from "@/features/care/server";
import { getWordRecords } from "@/features/record/server";
import { formatShortDate, formatShortMonth } from "@/utils/date";

import { growthStandardRanges } from "./growthStandardRanges";

/**
 * グラフ表示に必要なデータを一括取得・集計する
 *
 * @param isAdmin 管理者権限
 * @returns グラフ表示用のデータ
 * - heightData: 12ヶ月分の身長データ
 * - weightData: 12ヶ月分の体重データ
 * - milkData: 7日分のミルクデータ
 * - diaperData: 7日分の排泄データ
 * - wordData: 12ヶ月分のことばデータ
 * - careRecords: 7日分の全育児記録データ
 * - growthRecords: 12ヶ月分の成長記録データ
 * - wordRecords: ことば記録全データ
 */
export const getAndBuildGraphData = async (isAdmin: boolean) => {
  // 誕生日指定
  const birthYear = 2025;
  const birthMonth = 8;

  // 今日のまでの１週間分の指定
  const today = new Date();
  const careStartDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 6);
  const growthStartDate = new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());

  // 初期データを取得
  const [careRecords, growthRecords, wordRecords] = await Promise.all([
    isAdmin
      ? getCareRecords({ startDate: careStartDate, endDate: today })
      : Promise.resolve({ items: [] }),
    getGrowthRecords({ startDate: growthStartDate, endDate: today }),
    getWordRecords(),
  ]);

  // 1年で取得集計するデータの処理
  const heightData: { month: string; standardRange?: number[]; value: number | null }[] = [];
  const weightData: { month: string; standardRange?: number[]; value: number | null }[] = [];
  const wordData: { month: string; value: number }[] = [];

  for (let i = 0; i < 12; i++) {
    // 対象月の初日を取得してそこから対象の年と月を取得
    const targetMonthDate = new Date(today.getFullYear(), today.getMonth() - (11 - i), 1);
    const targetYear = targetMonthDate.getFullYear();
    const targetMonth = targetMonthDate.getMonth();

    // フォーマット用の文字列作成 (YYYY-MM)
    const monthKey = formatShortMonth(targetMonthDate);

    // 取得した成長データ(growthRecords)から対象月の身長/体重記録を抽出
    const heightRecordsInMonth = growthRecords.filter((record) => {
      return (
        // 記録年が対象年と一致 & 記録月が対象月と一致 & 対象のデータが存在している
        record.measurementDate.getFullYear() === targetYear &&
        record.measurementDate.getMonth() === targetMonth &&
        record.height != null
      );
    });
    const weightRecordsInMonth = growthRecords.filter((record) => {
      return (
        record.measurementDate.getFullYear() === targetYear &&
        record.measurementDate.getMonth() === targetMonth &&
        record.weight != null
      );
    });

    // 月に複数件ある場合の対処のため登録日時順にソート
    heightRecordsInMonth.sort((a, b) => {
      return b.measurementDate.getTime() - a.measurementDate.getTime();
    });

    weightRecordsInMonth.sort((a, b) => {
      return b.measurementDate.getTime() - a.measurementDate.getTime();
    });

    // 対象月初日時点の適正範囲算出のため月齢の算出
    // 年差を12か月に変換してから誕生月分を引いて対象月を加算する
    const monthsOld = (targetYear - birthYear) * 12 - (birthMonth - 1) + targetMonth;

    // 計算した月齢から該当するstandardRangeを取得
    const range = growthStandardRanges.find(
      (r) => monthsOld >= r.ageRangeMonths[0] && monthsOld < r.ageRangeMonths[1],
    );

    // 身長データを保存
    heightData.push({
      month: monthKey,
      standardRange: range?.heightRange,
      value: heightRecordsInMonth[0]?.height ?? null,
    });

    // 体重データを保存
    weightData.push({
      month: monthKey,
      standardRange: range?.weightRange,
      value: weightRecordsInMonth[0]?.weight ?? null,
    });

    // その月末までに登録された「ことば」の全記録を抽出
    const recordsUpToMonth = wordRecords.filter((record) => {
      // 以下条件に合うものを取得
      return (
        // 記録年が対象年より以前
        record.recordedDate.getFullYear() < targetYear ||
        // 記録年が対象年かつ記録月が対象月以前
        (record.recordedDate.getFullYear() === targetYear &&
          record.recordedDate.getMonth() <= targetMonth)
      );
    });

    // ことばデータを保存
    wordData.push({
      month: monthKey,
      value: recordsUpToMonth.length,
    });
  }

  // 1週間で取得集計するデータの処理
  const milkData: { day: string; value: number }[] = [];
  const diaperData: { day: string; value: number; secondValue: number }[] = [];

  // 対象日を取得する関数（後続の1週間ループ内処理の共通化）
  const targetDay = (base: Date, days: number) => {
    return new Date(base.getFullYear(), base.getMonth(), base.getDate() + days);
  };

  // 日付表示兼比較用の文字をデータに追加
  const formattedCareRecords = careRecords.items.map((record) => ({
    ...record,
    recordedAtStr: formatShortDate(record.recordedAt),
  }));

  for (let i = 0; i < 7; i++) {
    // 対象の日付を取得
    const targetDate = formatShortDate(targetDay(today, i - 6));

    // その日の記録だけを事前に絞り込み
    const recordsInDay = formattedCareRecords.filter(
      (record) => record.recordedAtStr === targetDate,
    );

    // データタイプ：MILKのamountMlデータを集計しミルク量を算出
    const totalMilkAmount = recordsInDay
      .filter((record) => record.recordType === "MILK")
      .reduce((sum, record) => sum + (record.milkDetail?.amountMl ?? 0), 0);

    // ミルクデータを保存
    milkData.push({
      day: targetDate,
      value: totalMilkAmount,
    });

    // データタイプ：DIAPERのWETデータをカウントし排泄記録を算出
    const wetCount = recordsInDay
      .filter(
        (record) => record.recordType === "DIAPER" && record.diaperDetail?.diaperType === "WET",
      )
      .reduce((sum) => sum + 1, 0);

    // データタイプ：DIAPERのDIRTYデータをカウントし排泄記録を算出
    const dirtyCount = recordsInDay
      .filter(
        (record) => record.recordType === "DIAPER" && record.diaperDetail?.diaperType === "DIRTY",
      )
      .reduce((sum) => sum + 1, 0);

    // 排泄データを保存
    diaperData.push({
      day: targetDate,
      value: wetCount,
      secondValue: dirtyCount,
    });
  }

  return {
    heightData,
    weightData,
    milkData,
    diaperData,
    wordData,
    careRecords,
    growthRecords,
    wordRecords,
  };
};
