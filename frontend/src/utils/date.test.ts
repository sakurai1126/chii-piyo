import { describe, it, expect, vi, afterAll, beforeAll } from "vitest";

import {
  calculateDaysSinceBirth,
  calculateRemainingDays,
  dateOnlyToUtcNoon,
  formatJapaneseDate,
  formatJapaneseDateBasic,
  formatJapaneseDateNonTime,
  formatJapaneseDateTimeOnly,
  formatShortDate,
  formatShortMonth,
  getCurrentDateTime,
} from "./date";

// テスト用にシステム時刻を2026-01-01 12:00:00に固定
beforeAll(() => {
  vi.useFakeTimers();
  vi.setSystemTime(new Date("2026-01-01T12:00:00"));
});

// テスト終了後にタイマーをシステム時刻に戻す
afterAll(() => {
  vi.useRealTimers();
});

describe("formatJapaneseDate", () => {
  it("Date-01: 有効な日付を渡した場合YYYY年M月D日 HH:MM形式の文字列が返ること", () => {
    expect(formatJapaneseDate(new Date("2026-01-01T12:00"))).toBe("2026年1月1日 12:00");
  });

  it("Date-02: 不正な日付文字列を渡した場合空文字が返ること", () => {
    expect(formatJapaneseDate(new Date("invalid"))).toBe("");
  });
});

describe("formatJapaneseDateNonTime", () => {
  it("Date-03: 有効な日付を渡した場合YYYY年M月D日形式の文字列が返ること", () => {
    expect(formatJapaneseDateNonTime(new Date("2026-01-01T12:00"))).toBe("2026年1月1日");
  });

  it("Date-04: 不正な日付文字列を渡した場合空文字が返ること", () => {
    expect(formatJapaneseDateNonTime(new Date("invalid"))).toBe("");
  });
});

describe("formatShortDate", () => {
  it("Date-05: 有効な日付を渡した場合M/D形式の文字列が返ること", () => {
    expect(formatShortDate(new Date("2026-01-01"))).toBe("1/1");
  });

  it("Date-06: 不正な日付文字列を渡した場合空文字が返ること", () => {
    expect(formatShortDate(new Date("invalid"))).toBe("");
  });
});

describe("formatShortMonth", () => {
  it("Date-07: 有効な日付を渡した場合YYYY/M形式の文字列が返ること", () => {
    expect(formatShortMonth(new Date("2026-01-01"))).toBe("2026/1");
  });

  it("Date-08: 不正な日付文字列を渡した場合空文字が返ること", () => {
    expect(formatShortMonth(new Date("invalid"))).toBe("");
  });
});

describe("formatJapaneseDateBasic", () => {
  it("Date-09: 有効な日付を渡した場合YYYY-MM-DD形式の文字列が返ること", () => {
    expect(formatJapaneseDateBasic(new Date("2026-01-01"))).toBe("2026-01-01");
  });

  it("Date-10: 不正な日付文字列を渡した場合空文字が返ること", () => {
    expect(formatJapaneseDateBasic(new Date("invalid"))).toBe("");
  });
});

describe("formatJapaneseDateTimeOnly", () => {
  it("Date-11: 有効な日付を渡した場合HH:MM形式の文字列が返ること", () => {
    expect(formatJapaneseDateTimeOnly(new Date("2026-01-01T12:00"))).toBe("12:00");
  });

  it("Date-12: 不正な日付文字列を渡した場合空文字が返ること", () => {
    expect(formatJapaneseDateTimeOnly(new Date("invalid"))).toBe("");
  });
});

describe("calculateRemainingDays", () => {
  it("Date-13: 未来の期限日を渡した場合残り日数が返ること", () => {
    expect(calculateRemainingDays(new Date("2026-01-02T12:00"))).toBe(1);
  });

  it("Date-14: 期限日が当日または過去日付の場合0が返ること", () => {
    expect(calculateRemainingDays(new Date("2026-01-01T12:00"))).toBe(0);
    expect(calculateRemainingDays(new Date("2025-12-31T12:00"))).toBe(0);
  });
});

describe("getCurrentDateTime", () => {
  it("Date-15: 現在日時と現在時刻が返ること", () => {
    const { currentDate, currentTime } = getCurrentDateTime();
    expect(currentDate).toBe("2026-01-01");
    expect(currentTime).toBe("12:00");
  });
});

describe("calculateDaysSinceBirth", () => {
  it("Date-16: 誕生日からの経過日付を渡すとX年Yヶ月Z日形式の経過日数文字列が返ること", () => {
    expect(calculateDaysSinceBirth(new Date("2026-01-01"), new Date("2027-03-04"))).toBe(
      "1年2ヶ月3日",
    );
  });

  it("Date-17: 月跨ぎで日繰り下げが発生する日付を渡すと前月末日を加算した日繰り下げ計算結果が返ること", () => {
    expect(calculateDaysSinceBirth(new Date("2026-01-15"), new Date("2026-02-01"))).toBe("17日");
  });

  it("Date-18: 誕生日より前の日付を渡すと0日が返ること", () => {
    expect(calculateDaysSinceBirth(new Date("2026-01-01"), new Date("2025-12-31"))).toBe("0日");
  });
});

describe("dateOnlyToUtcNoon", () => {
  it("Date-19: YYYY-MM-DD形式の日付文字列を渡すとUTC正午が返ること", () => {
    expect(dateOnlyToUtcNoon("2026-01-01")).toEqual(new Date("2026-01-01T12:00:00Z"));
  });
});
