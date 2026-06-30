import { CareRecordResponseDto } from "@/lib/api-client/gen";

import { UpdateDataParams } from "../types";

export const generateUpdateCareRecordActionParams = (
  record: CareRecordResponseDto,
  updateData: UpdateDataParams,
) => {
  return {
    id: record.id,
    recordType: record.recordType,
    recordedAt: new Date(`${updateData.date}T${updateData.time}`),
    // 各種別ごとのデータ
    mealDetail:
      record.recordType === "MEAL"
        ? {
            // 元データ
            ...record.mealDetail,
            // メモ
            note: updateData.note,
          }
        : undefined,
    milkDetail:
      record.recordType === "MILK" && updateData.amountMl !== undefined
        ? {
            // 元データ
            ...record.milkDetail,
            // ミルク量
            amountMl: updateData.amountMl,
            // メモ
            note: updateData.note,
          }
        : undefined,
    diaperDetail:
      record.recordType === "DIAPER" && updateData.diaperType != null
        ? {
            // 元データ
            ...record.diaperDetail,
            // 排泄タイプ
            diaperType: updateData.diaperType as "DIRTY" | "WET",
            // メモ
            note: updateData.note,
          }
        : undefined,
    healthDetail:
      record.recordType === "HEALTH" && updateData.temperature != null
        ? {
            // 元データ
            ...record.healthDetail,
            // 体温
            temperature: updateData.temperature,
            // メモ
            note: updateData.note,
          }
        : undefined,
  };
};
