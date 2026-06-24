"use client";

import { useState } from "react";

export const useCalendar = () => {
  const weeklyText = ["日", "月", "火", "水", "木", "金", "土"];

  // 週の始まり（日曜日）を計算する
  const setStartDayFunc = (day: Date) => {
    return new Date(day.getFullYear(), day.getMonth(), day.getDate() - day.getDay());
  };

  // 今日の日付を取得
  const today = new Date();

  // 起点日を取得
  const [startDay, setStartDay] = useState<Date>(setStartDayFunc(today));

  // 現在表示する日付
  const [currentDay, setCurrentDay] = useState<Date>(today);

  // 現在を含む週かどうか
  const [isTodayWeek, setIsTodayWeek] = useState<boolean>(true);

  // 一週間分のオブジェクトを取得
  const [weeklyDates, setWeeklyDates] = useState<Date[]>(() =>
    Array.from({ length: 7 }, (_, index) => {
      const date = new Date(startDay);
      date.setDate(date.getDate() + index);
      return date;
    }),
  );

  // 週変更処理
  const changeWeek = (changeDay: number, specificDay?: Date) => {
    // 受け取った引数分をずらした週の始まりを取得
    const newDateTemp = new Date(startDay);
    newDateTemp.setDate(newDateTemp.getDate() + changeDay);
    const newStartDay = setStartDayFunc(newDateTemp);

    // 週の起点を更新
    setStartDay(newStartDay);
    // 表示日時を更新 - 引数があればその日を、なければ週の起点を設定
    setCurrentDay(specificDay || newStartDay);

    // 新しい一週間分のオブジェクトを生成
    const newWeekDates = Array.from({ length: 7 }, (_, index) => {
      const date = new Date(newStartDay);
      date.setDate(date.getDate() + index);
      return date;
    });

    // 週表示を更新
    setWeeklyDates(newWeekDates);

    // 今週かどうかを判定
    setIsTodayWeek(
      newWeekDates.some(
        (date) =>
          date.getFullYear() === today.getFullYear() &&
          date.getMonth() === today.getMonth() &&
          date.getDate() === today.getDate(),
      ),
    );
  };

  // 日付変更処理
  const changeDays = (changeDay: number) => {
    // 受け取った引数で表示する日付を更新
    const newDate = new Date(currentDay);
    newDate.setDate(newDate.getDate() + changeDay);
    setCurrentDay(newDate);

    // 新しい日付が表示中の週かどうかを判定
    const isCurrentWeek = weeklyDates.some(
      (date) =>
        date.getFullYear() === newDate.getFullYear() &&
        date.getMonth() === newDate.getMonth() &&
        date.getDate() === newDate.getDate(),
    );

    // 上記判定外の場合更新処理を行う
    if (!isCurrentWeek) {
      // 先にずらすか前にずらすのかを判定
      if (newDate < startDay) {
        changeWeek(-7, newDate);
      } else {
        changeWeek(7, newDate);
      }
    }
  };

  return {
    weeklyText,
    today,
    startDay,
    currentDay,
    isTodayWeek,
    weeklyDates,
    changeWeek,
    changeDays,
  };
};
