/**
 * Date型の情報をフォーマットした文字列で返す
 *
 * @param date Date型の日付情報
 * @returns "YYYY年MM月DD日 HH:MM"形式の文字列
 */
export const formatJapaneseDate = (dateParam: string | Date): string => {
  const date = typeof dateParam === "string" ? new Date(dateParam) : dateParam;

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
export const formatJapaneseDateNonTime = (dateParam: string | Date): string => {
  const date = typeof dateParam === "string" ? new Date(dateParam) : dateParam;

  if (Number.isNaN(date.getTime())) return "";

  return new Intl.DateTimeFormat("ja-JP", {
    dateStyle: "long",
  }).format(date);
};

/**
 * Date型の情報をフォーマットした文字列で返す
 *
 * @param date Date型の日付情報
 * @returns "MM/DD"形式の文字列
 */
export const formatShortDate = (dateParam: string | Date): string => {
  const date = typeof dateParam === "string" ? new Date(dateParam) : dateParam;

  if (Number.isNaN(date.getTime())) return "";

  // タイムゾーンを日本時間に指定してフォーマット（en-USで月/日形式にする）
  return new Intl.DateTimeFormat("en-US", {
    timeZone: "Asia/Tokyo",
    month: "numeric",
    day: "numeric",
  }).format(date);
};

/**
 * Date型の情報をフォーマットした文字列で返す
 *
 * @param date Date型の日付情報
 * @returns "YYYY-MM-DD"形式の文字列
 */
export const formatJapaneseDateBasic = (dateParam: string | Date): string => {
  const date = typeof dateParam === "string" ? new Date(dateParam) : dateParam;

  if (Number.isNaN(date.getTime())) return "";

  // inputに渡すことも想定し0埋めの2桁形式で返却
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

/**
 * Date型の情報をフォーマットした文字列で返す（時刻のみ）
 *
 * @param date Date型の日付情報
 * @returns "HH:MM"形式の文字列
 */
export const formatJapaneseDateTimeOnly = (dateParam: string | Date): string => {
  const date = typeof dateParam === "string" ? new Date(dateParam) : dateParam;

  if (Number.isNaN(date.getTime())) return "";

  // inputに渡すことも想定し0埋めの2桁形式で返却
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${hours}:${minutes}`;
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

/**
 * 誕生日から指定した日付までの経過日数を計算する
 * @param birthday 誕生日
 * @param targetDate 対象となる日付
 * @returns 経過日数
 */
export const calculateDaysSinceBirth = (
  birthday: Date | string,
  targetDate: Date | string,
): string => {
  // 誕生日をセット、時刻は不要なので0にリセット
  const birth = new Date(birthday);
  birth.setHours(0, 0, 0, 0);

  // 指定日付をセット
  const target = new Date(targetDate);
  target.setHours(0, 0, 0, 0);

  // 記録日が誕生日より前の場合は0日で返却
  if (target.getTime() < birth.getTime()) {
    return "0日";
  }

  // 年月日それぞれの差分を計算
  let years = target.getFullYear() - birth.getFullYear();
  let months = target.getMonth() - birth.getMonth();
  let days = target.getDate() - birth.getDate();

  // 日が0以下の場合日数の繰り下げ処理
  if (days < 0) {
    months--;
    // 第3引数に0を指定し、前月の末日を取得して加算
    const prevMonthLastDay = new Date(target.getFullYear(), target.getMonth(), 0).getDate();
    days += prevMonthLastDay;
  }

  // 月が0以下の場合月数の繰り下げ処理
  if (months < 0) {
    years--;
    months += 12;
  }

  // 文字列の組み立て
  let result = "";

  // 年がある場合年表示追加
  if (years > 0) {
    result += `${years}年`;
  }

  // 年または月がある場合月表示追加
  if (years > 0 || months > 0) {
    result += `${months}ヶ月`;
  }

  // 日表示を追加
  result += `${days}日`;
  return result;
};
