import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { toast } from "@/components/ui/Toast";
import { ActionResult } from "@/utils/action";
import { formatJapaneseDateBasic } from "@/utils/date";

import { createFirstRecordAction } from "../actions/createFirstRecordAction";
import { updateFirstRecordAction } from "../actions/updateFirstRecordAction";
import { FirstRecordData, SelectedMediaData } from "../types";

type Props = {
  setIsMenuOpen: Dispatch<SetStateAction<boolean>>;
  initialEditData?: FirstRecordData;
  variant: "newFirstRecord" | "editFirstRecord" | "newWordRecord" | "editWordRecord";
};

/**
 * はじめて記録/ことば記録の新規作成UIを操作するためのカスタムフック
 */
export const useRecordEdit = ({ setIsMenuOpen, initialEditData, variant }: Props) => {
  // 保存時確認モーダル
  const [isSaveConfirmOpen, setIsSaveConfirmOpen] = useState<boolean>(false);

  // メディア追加モーダル
  const [isAddMediaModalOpen, setIsAddMediaModalOpen] = useState<boolean>(false);

  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // メディア追加の状態管理
  const [selectedMediaData, setSelectedMediaData] = useState<SelectedMediaData[]>(
    initialEditData?.media?.map((media) => ({
      id: media.id,
      url: media.url,
    })) ?? [],
  );

  // 入力データ初期値
  const initialData = {
    title: initialEditData?.title ?? "",
    achievedDate: initialEditData?.achievedDate ?? formatJapaneseDateBasic(new Date()),
    comment: initialEditData?.comment ?? "",
  };

  // 入力データ管理
  const [data, setData] = useState<FirstRecordData>(initialData);

  // 確認画面の表示と表示前バリデーション
  const confirmOpen = () => {
    if (!data.title) {
      toast.error("記録内容を入力してください");
      return;
    }

    if (!data.achievedDate) {
      toast.error("日時を入力してください");
      return;
    }

    // 確認モーダルの表示
    setIsSaveConfirmOpen(true);
  };

  // 編集キャンセル処理
  const cancelEdit = () => {
    // 状態の初期化（入力メニュー/モーダル開閉/入力内容/メディア選択）
    setIsMenuOpen(false);
    setIsSaveConfirmOpen(false);
    setData(initialData);
    setSelectedMediaData([]);
  };

  // はじめて記録の新規作成処理
  const saveNewFirstRecord = async () => {
    const result = await createFirstRecordAction({
      title: data.title,
      achievedDate: new Date(data.achievedDate),
      comment: data.comment,
      mediaIds: selectedMediaData.map((media) => media.id),
    });

    afterSaveAction(result, "はじめて記録を作成しました");
  };

  // はじめて記録の更新処理
  const updateFirstRecord = async () => {
    if (!initialEditData?.id) {
      toast.error("はじめて記録が見つかりませんでした");
      return;
    }

    const result = await updateFirstRecordAction({
      id: initialEditData.id,
      title: data.title,
      achievedDate: new Date(data.achievedDate),
      comment: data.comment,
      mediaIds: selectedMediaData.map((media) => media.id),
    });

    afterSaveAction(result, "はじめて記録を更新しました");
  };

  // 保存後の後処理（UI更新/状態初期化/通知）
  const afterSaveAction = (result: ActionResult, successMessage: string) => {
    if (result.success) {
      // 状態の初期化（入力メニュー/モーダル開閉/入力内容/メディア選択）
      setIsMenuOpen(false);
      setIsSaveConfirmOpen(false);
      setData(initialData);
      setSelectedMediaData([]);

      // 通知処理
      toast.success(successMessage);
    } else {
      toast.error(result.error);
    }
  };

  // 保存処理
  const saveAction = () => {
    startTransition(async () => {
      switch (variant) {
        case "newFirstRecord":
          saveNewFirstRecord();
          break;
        case "editFirstRecord":
          updateFirstRecord();
          break;
        case "newWordRecord":
          break;
        case "editWordRecord":
          break;
        default:
          break;
      }
    });
  };

  return {
    isSaveConfirmOpen,
    isAddMediaModalOpen,
    setIsAddMediaModalOpen,
    isPending,
    selectedMediaData,
    setSelectedMediaData,
    setIsSaveConfirmOpen,
    data,
    setData,
    confirmOpen,
    cancelEdit,
    saveAction,
  };
};
