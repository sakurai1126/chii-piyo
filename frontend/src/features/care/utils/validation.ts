import { toast } from "@/components/ui/Toast";
import { CareRecordResponseDto } from "@/lib/api-client/gen";

import { UpdateDataParams } from "../types";

// バリデーション関数
export const validateCareRecordUpdate = (
  careRecords: CareRecordResponseDto,
  updateData: UpdateDataParams,
): boolean => {
  if (careRecords.recordType === "MILK") return validateMilkUpdate(updateData);
  if (careRecords.recordType === "DIAPER") return validateDiaperUpdate(updateData);
  if (careRecords.recordType === "HEALTH") return validateHealthUpdate(updateData);
  return true;
};

// ミルク記録バリデーション
const validateMilkUpdate = (updateData: UpdateDataParams): boolean => {
  if (!updateData.amountMl) {
    toast.error("ミルク量を入力してください。");
    return false;
  }
  if (updateData.amountMl > 400) {
    toast.error("ミルク量を400ml以内で入力してください。");
    return false;
  }
  if (updateData.amountMl < 10) {
    toast.error("ミルク量を10ml以上で入力してください。");
    return false;
  }
  return true;
};

// 排泄記録バリデーション
const validateDiaperUpdate = (updateData: UpdateDataParams): boolean => {
  if (!updateData.diaperType) {
    toast.error("排泄タイプを入力してください。");
    return false;
  }
  if (updateData.diaperType !== "DIRTY" && updateData.diaperType !== "WET") {
    toast.error("排泄タイプが不正です。");
    return false;
  }
  return true;
};

// 体調記録バリデーション
const validateHealthUpdate = (updateData: UpdateDataParams): boolean => {
  if (!updateData.temperature) {
    toast.error("体温を入力してください。");
    return false;
  }
  if (updateData.temperature < 34 || updateData.temperature > 42) {
    toast.error("体温を正しく入力してください");
    return false;
  }
  return true;
};

export const validateGrowthRecordUpdate = (updateData: UpdateDataParams) => {
  if (!updateData.height && !updateData.weight) {
    toast.error("身長または体重を入力してください");
    return false;
  }

  if (updateData.height && (updateData.height <= 0 || updateData.height > 200)) {
    toast.error("身長を正しく入力してください");
    return false;
  }

  if (updateData.weight && (updateData.weight <= 0 || updateData.weight > 200)) {
    toast.error("体重を正しく入力してください");
    return false;
  }
  return true;
};
