/**
 * Date型の情報をフォーマットした文字列で返す
 *
 * @param date Date型の日付情報
 * @returns "YYYY年MM月DD日 HH:MM"形式の文字列
 */
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

/**
 * Date型の情報をフォーマットした文字列で返す（時刻なし）
 *
 * @param date Date型の日付情報
 * @returns "YYYY年MM月DD日"形式の文字列
 */
export const formatJapaneseDateNonTime = (date: Date): string => {
  if (isNaN(date.getTime())) return "";

  return new Intl.DateTimeFormat("ja-JP", {
    dateStyle: "long",
  }).format(date);
};
