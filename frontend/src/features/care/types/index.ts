// 更新用データ保持
export type UpdateDataParams = {
  date: string;
  time: string;
  note: string;
  amountMl: number | undefined;
  diaperType: string | undefined;
  temperature: number | null | undefined;
  height: number | undefined;
  weight: number | undefined;
};
