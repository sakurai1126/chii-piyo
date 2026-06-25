/**
 * Date型の情報をフォーマットした文字列で返す
 *
 * @param date Date型の日付情報
 * @returns "YYYY年MM月DD日 HH:MM"形式の文字列
 */
export const formatJapaneseDate = (date: Date): string => {
  // 不正な日付文字列の対策
  if (Number.isNaN(date.getTime())) return "";

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
  if (Number.isNaN(date.getTime())) return "";

  return new Intl.DateTimeFormat("ja-JP", {
    dateStyle: "long",
  }).format(date);
};

/**
 * 指定された日付までの残り日数を計算する
 * @param expiresAt 削除予定日時
 * @returns 残り日数
 */
export const calculateRemainingDays = (expiresAt: string | Date): number => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const expireDate = new Date(expiresAt);
  expireDate.setHours(0, 0, 0, 0);
  // 差分をミリ秒から日数に変換
  const diffTime = expireDate.getTime() - today.getTime();
  const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));
  return Math.max(0, diffDays);
};

/**
 * 現在の日時を返す
 * @returns
 * - currentDate YYYY-MM-DD形式の現在日時
 * - currentTime HH:MM形式の現在時刻
 */
export const getCurrentDateTime = () => {
  // 現在の日時を取得
  const now = new Date();

  // YYYY-MM-DD形式に変換
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const currentDate = `${year}-${month}-${day}`;

  // HH:MM形式に変換
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const currentTime = `${hours}:${minutes}`;

  return { currentDate, currentTime };
};
