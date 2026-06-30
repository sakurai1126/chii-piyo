"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useId, useRef, useState, useTransition } from "react";

import { toast } from "@/components/ui/Toast";
import { CareRecordResponseDto, GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateBasic, formatJapaneseDateTimeOnly } from "@/utils/date";

import { updateCareRecordAction } from "../actions/updateCareRecordAction";
import { updateGrowthRecordAction } from "../actions/updateGrowthRecordAction";
import diaperIcon from "../assets/diaper.svg";
import healthIcon from "../assets/health.svg";
import mealIcon from "../assets/meal.svg";
import milkIcon from "../assets/milk.svg";
import { UpdateDataParams } from "../types";
import { generateUpdateCareRecordActionParams } from "../utils/generateParams";
import { validateCareRecordUpdate, validateGrowthRecordUpdate } from "../utils/validation";

type Props = {
  state: {
    isPopOpen: boolean;
    top: number;
    left: number;
    record: CareRecordResponseDto | null;
    growthRecord: GrowthRecordResponseDto | null;
  };
  popCloseAction: () => void;
};
export const useCalendarPop = ({ state, popCloseAction }: Props) => {
  const uid = useId();
  const dataMap = {
    MEAL: { icon: mealIcon, label: "食事", color: "text-meal-text" },
    MILK: { icon: milkIcon, label: "ミルク", color: "text-milk-text" },
    DIAPER: { icon: diaperIcon, label: "排泄", color: "text-diaper-text" },
    HEALTH: { icon: healthIcon, label: "体調", color: "text-health-text" },
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

  const [updateData, setUpdateData] = useState<UpdateDataParams>({
    date: "",
    time: "",
    note: "",
    amountMl: undefined,
    diaperType: undefined,
    temperature: undefined,
    height: undefined,
    weight: undefined,
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
        height: undefined,
        weight: undefined,
      });
    }

    if (state.growthRecord) {
      const recordDate = new Date(state.growthRecord.measurementDate);
      setUpdateData({
        date: formatJapaneseDateBasic(recordDate),
        time: "00:00",
        note: state.growthRecord.note || "",
        amountMl: undefined,
        diaperType: undefined,
        temperature: undefined,
        height: state.growthRecord.height,
        weight: state.growthRecord.weight,
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

  const saveCareRecordAction = () => {
    if (!state.record) {
      toast.error("エラーが発生しました。");
      return;
    }

    const record = state.record;

    // 入力値バリデーション
    if (!validateCareRecordUpdate(record, updateData)) return;

    startTransition(async () => {
      const result = await updateCareRecordAction(
        // 種別ごとに合わせたパラメータを作成
        generateUpdateCareRecordActionParams(record, updateData),
      );

      // 更新処理
      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["careRecords"] });
        popCloseAction();
        toast.success("育児記録を更新しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const saveGrowthRecordAction = () => {
    if (!state.growthRecord) {
      toast.error("エラーが発生しました");
      return;
    }
    const growthRecord = state.growthRecord;

    if (!validateGrowthRecordUpdate(updateData)) return;

    startTransition(async () => {
      const result = await updateGrowthRecordAction({
        id: growthRecord.id,
        measurementDate: new Date(updateData.date),
        height: updateData.height,
        weight: updateData.weight,
        note: updateData.note,
      });
      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["growthRecords"] });
        popCloseAction();
        toast.success("成長記録を更新しました");
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
    saveCareRecordAction,
    saveGrowthRecordAction,
  };
};
