"use client";

import { useState, useTransition } from "react";

import { getCurrentDateTime } from "@/utils/date";

export const useCareRecord = () => {
  // モーダル表示フラグ
  const [isOpen, setIsOpen] = useState<boolean>(false);

  // 二重送信制御
  const [isPending, startTransition] = useTransition();

  // メモ管理
  const [note, setNote] = useState<string>("");

  // 現在日時を取得
  const { currentDate, currentTime } = getCurrentDateTime();

  // 記録日時管理
  const [date, setDate] = useState<string>(currentDate);
  const [time, setTime] = useState<string>(currentTime);

  const openModal = () => {
    const { currentDate: newDate, currentTime: newTime } = getCurrentDateTime();
    setDate(newDate);
    setTime(newTime);
    setIsOpen(true);
  };

  return {
    isOpen,
    setIsOpen,
    isPending,
    startTransition,
    note,
    setNote,
    date,
    setDate,
    time,
    setTime,
    openModal,
  };
};
