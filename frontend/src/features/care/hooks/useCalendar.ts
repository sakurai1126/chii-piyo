"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef, useState, useTransition } from "react";

import { toast } from "@/components/ui/Toast";
import {
  CareRecordListResponseDto,
  CareRecordResponseDto,
  GrowthRecordResponseDto,
} from "@/lib/api-client/gen";

import { deleteCareRecordAction } from "../actions/deleteCareRecordAction";
import { deleteGrowthRecordAction } from "../actions/deleteGrowthRecordAction";

import { useGetCareRecords } from "./useGetCareRecords";
import { useGetGrowthRecords } from "./useGetGrowthRecords";

type Params = {
  initialCareRecords: CareRecordListResponseDto;
  initialGrowthRecords: GrowthRecordResponseDto[];
};

export const useCalendar = ({ initialCareRecords, initialGrowthRecords }: Params) => {
  const weeklyText = ["日", "月", "火", "水", "木", "金", "土"];

  // 週の始まり（日曜日）を計算する
  const setStartDayFunc = (day: Date) => {
    return new Date(day.getFullYear(), day.getMonth(), day.getDate() - day.getDay());
  };

  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 今日の日付を取得
  const today = new Date();

  // 起点日を取得
  const [startDay, setStartDay] = useState<Date>(setStartDayFunc(today));

  // 現在表示する日付
  const [currentDay, setCurrentDay] = useState<Date>(today);

  // 現在を含む週かどうか
  const [isTodayWeek, setIsTodayWeek] = useState<boolean>(true);

  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // 一週間分のオブジェクトを取得
  const [weeklyDates, setWeeklyDates] = useState<Date[]>(() =>
    Array.from({ length: 7 }, (_, index) => {
      const date = new Date(startDay);
      date.setDate(date.getDate() + index);
      return date;
    }),
  );

  // 表示の終了日
  const endDay = new Date(startDay.getFullYear(), startDay.getMonth(), startDay.getDate() + 7);

  // 育児記録を取得
  const { data: careRecords } = useGetCareRecords({
    startDate: startDay,
    endDate: endDay,
    // 表示が現在の週の場合はサーバーで取得した初期値を使用する
    initialData: isTodayWeek ? initialCareRecords : undefined,
  });

  // 成長記録を取得
  const { data: growthRecords } = useGetGrowthRecords({
    startDate: startDay,
    endDate: endDay,
    // 表示が現在の週の場合はサーバーで取得した初期値を使用する
    initialData: isTodayWeek ? initialGrowthRecords : undefined,
  });

  const [pop, setPop] = useState<{
    isPopOpen: boolean;
    top: number;
    left: number;
    record: CareRecordResponseDto | null;
    growthRecord: GrowthRecordResponseDto | null;
    weekIndex: number;
  }>({
    isPopOpen: false,
    top: 0,
    left: 0,
    record: null,
    growthRecord: null,
    weekIndex: 0,
  });

  // 直前に開いていたポップアップのデータを保持する
  // 要素外クリックで閉じる処理によって削除時のボタンクリックにも影響してしまうためバックアップを管理
  const deleteTargetRef = useRef(pop);
  useEffect(() => {
    // pop にデータがセットされた時だけ更新
    if (pop.record || pop.growthRecord) {
      deleteTargetRef.current = pop;
    }
  }, [pop]);

  const itemsTapAction = ({
    item,
    growthItem,
    event,
    weekIndex,
  }: {
    item: CareRecordResponseDto | null;
    growthItem: GrowthRecordResponseDto | null;
    event: React.MouseEvent<HTMLButtonElement>;
    weekIndex: number;
  }) => {
    // 要素が持つ、親要素基準の相対位置を直接取得
    const top = event.currentTarget.offsetTop;
    const left = event.currentTarget.offsetLeft;
    setPop({
      isPopOpen: true,
      top,
      left,
      record: item,
      growthRecord: growthItem,
      weekIndex,
    });
  };

  const popCloseAction = () => {
    setPop({
      isPopOpen: false,
      top: 0,
      left: 0,
      record: null,
      growthRecord: null,
      weekIndex: 0,
    });
  };

  // 週変更処理
  const changeWeek = (changeDay: number, specificDay?: Date) => {
    popCloseAction();
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
    popCloseAction();
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

  // 削除モーダル開閉フラグ
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState<boolean>(false);

  // 削除処理
  const deleteAction = () => {
    // 直前の mousedown で pop がリセットされている場合はバックアップを使用する
    const targetRecord = pop.record || deleteTargetRef.current.record;
    const targetGrowth = pop.growthRecord || deleteTargetRef.current.growthRecord;

    if (!targetRecord && !targetGrowth) {
      toast.error("不正なアクセスです。");
      return;
    }

    startTransition(async () => {
      if (targetRecord) {
        const result = await deleteCareRecordAction({
          id: targetRecord.id,
        });

        if (result.success) {
          queryClient.invalidateQueries({ queryKey: ["careRecords"] });
          popCloseAction();
          setIsDeleteConfirmOpen(false);
          toast.success("育児記録の削除に成功しました");
        } else {
          toast.error(result.error);
        }
      }

      if (targetGrowth) {
        const result = await deleteGrowthRecordAction({
          id: targetGrowth.id,
        });

        if (result.success) {
          queryClient.invalidateQueries({ queryKey: ["growthRecords"] });
          popCloseAction();
          setIsDeleteConfirmOpen(false);
          toast.success("成長記録の削除に成功しました");
        } else {
          toast.error(result.error);
        }
      }
    });
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
    careRecords,
    growthRecords,
    pop,
    itemsTapAction,
    popCloseAction,
    isPending,
    isDeleteConfirmOpen,
    setIsDeleteConfirmOpen,
    deleteAction,
  };
};
