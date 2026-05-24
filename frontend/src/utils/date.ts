export const formatJapaneseDate = (date: Date): string => {
  // 不正な日付文字列の対策
  if (isNaN(date.getTime())) return "";

  // dateStyle: 'long' -> "2026年1月1日"
  // timeStyle: 'short' -> "12:00"
  return new Intl.DateTimeFormat("ja-JP", {
    dateStyle: "long",
    timeStyle: "short",
  }).format(date);
};
