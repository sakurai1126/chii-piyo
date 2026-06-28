"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useId, useRef, useState, useTransition } from "react";

import { toast } from "@/components/ui/Toast";
import { CareRecordResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateBasic, formatJapaneseDateTimeOnly } from "@/utils/date";

import { updateCareRecordAction } from "../actions/updateCareRecordAction";
import diaperIcon from "../assets/diaper.svg";
import healthIcon from "../assets/health.svg";
import mealIcon from "../assets/meal.svg";
import milkIcon from "../assets/milk.svg";

type Props = {
  state: {
    isPopOpen: boolean;
    top: number;
    left: number;
    record: CareRecordResponseDto | null;
  };
  popCloseAction: () => void;
};
export const useCalendarPop = ({ state, popCloseAction }: Props) => {
  const uid = useId();
  const dataMap = {
    MEAL: {
      icon: mealIcon,
      label: "食事",
      color: "text-meal-text",
    },
    MILK: {
      icon: milkIcon,
      label: "ミルク",
      color: "text-milk-text",
    },
    DIAPER: {
      icon: diaperIcon,
      label: "排泄",
      color: "text-diaper-text",
    },
    HEALTH: {
      icon: healthIcon,
      label: "体調",
      color: "text-health-text",
    },
  };

  // 編集モード管理
  const [isEditMode, setIsEditMode] = useState<boolean>(false);

  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // 開閉時や別ポップを開いたときに編集モードを解除する
  useEffect(() => {
    return () => setIsEditMode(false);
  }, [state.isPopOpen]);

  const [updateData, setUpdateData] = useState<{
    date: string;
    time: string;
    note: string;
    amountMl: number | undefined;
    diaperType: string | undefined;
    temperature: number | null | undefined;
  }>({
    date: "",
    time: "",
    note: "",
    amountMl: undefined,
    diaperType: undefined,
    temperature: undefined,
  });

  const popRef = useRef<HTMLDivElement>(null);

  const editModeOpen = () => {
    if (state.record) {
      const recordDate = new Date(state.record.recordedAt);
      setUpdateData({
        date: formatJapaneseDateBasic(recordDate),
        time: formatJapaneseDateTimeOnly(recordDate),
        note:
          state.record.mealDetail?.note ||
          state.record.milkDetail?.note ||
          state.record.diaperDetail?.note ||
          state.record.healthDetail?.note ||
          "",
        amountMl: state.record.milkDetail?.amountMl,
        diaperType: state.record.diaperDetail?.diaperType,
        temperature: state.record.healthDetail?.temperature,
      });
    }
    setIsEditMode(true);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      // popRefが存在し、かつクリックされた要素がpopRefの内側ではない場合
      if (popRef.current && !popRef.current.contains(event.target as Node)) {
        popCloseAction();
      }
    };
    // マウスダウンイベントを監視（スマホ対応も含める場合は "touchstart" を併用することも多いです）
    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [popCloseAction]);

  const saveAction = () => {
    if (!state.record) {
      toast.error("エラーが発生しました。");
      return;
    }

    const record = state.record;

    if (record.recordType === "MILK") {
      if (!updateData.amountMl) {
        toast.error("ミルク量を入力してください。");
        return;
      }

      if (updateData.amountMl > 400) {
        toast.error("ミルク量を400ml以内で入力してください。");
        return;
      }

      if (updateData.amountMl < 10) {
        toast.error("ミルク量を10ml以上で入力してください。");
        return;
      }
    }

    if (record.recordType === "DIAPER") {
      if (!updateData.diaperType) {
        toast.error("排泄タイプを入力してください。");
        return;
      }

      if (updateData.diaperType !== "DIRTY" && updateData.diaperType !== "WET") {
        toast.error("排泄タイプが不正です。");
        return;
      }
    }

    if (record.recordType === "HEALTH") {
      if (!updateData.temperature) {
        toast.error("体温を入力してください。");
        return;
      }

      if (updateData.temperature < 34 || updateData.temperature > 42) {
        toast.error("体温を正しく入力してください");
        return;
      }
    }

    startTransition(async () => {
      const result = await updateCareRecordAction({
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
          record.recordType === "MILK"
            ? {
                // 元データ
                ...record.milkDetail,
                // ミルク量
                amountMl: updateData.amountMl!,
                // メモ
                note: updateData.note,
              }
            : undefined,
        diaperDetail:
          record.recordType === "DIAPER"
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
          record.recordType === "HEALTH"
            ? {
                // 元データ
                ...record.healthDetail,
                // 体温
                temperature: updateData.temperature!,
                // メモ
                note: updateData.note,
              }
            : undefined,
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["careRecords"] });
        popCloseAction();
        toast.success("記録を更新しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return {
    uid,
    dataMap,
    isEditMode,
    setIsEditMode,
    isPending,
    updateData,
    setUpdateData,
    popRef,
    editModeOpen,
    saveAction,
  };
};
