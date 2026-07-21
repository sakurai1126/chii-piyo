import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { toast } from "@/components/ui/Toast";
import { ActionResult } from "@/utils/action";
import { formatJapaneseDateBasic } from "@/utils/date";

import { createFirstRecordAction } from "../actions/createFirstRecordAction";
import { createWordRecordAction } from "../actions/createWordRecordAction";
import { updateFirstRecordAction } from "../actions/updateFirstRecordAction";
import { updateWordRecordAction } from "../actions/updateWordRecordAction";
import { RecordData, SelectedMediaData } from "../types";

type Props = {
  setIsMenuOpen: Dispatch<SetStateAction<boolean>>;
  initialEditData?: RecordData;
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
    recordedDate: initialEditData?.recordedDate ?? formatJapaneseDateBasic(new Date()),
    comment: initialEditData?.comment ?? "",
  };

  // 入力データ管理
  const [data, setData] = useState<RecordData>(initialData);

  // 確認画面の表示と表示前バリデーション
  const confirmOpen = () => {
    if (!data.title) {
      toast.error("記録内容を入力してください");
      return;
    }

    if (!data.recordedDate) {
      toast.error("日時を入力してください");
      return;
    }

    // 確認モーダルの表示
    setIsSaveConfirmOpen(true);
  };

  // メディア削除処理
  const removeMedia = (targetId: number) => {
    setSelectedMediaData((prev) => prev.filter((media) => media.id !== targetId));
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
      achievedDate: data.recordedDate,
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
      recordedDate: data.recordedDate,
      comment: data.comment,
      mediaIds: selectedMediaData.map((media) => media.id),
    });

    afterSaveAction(result, "はじめて記録を更新しました");
  };

  // ことばの記録の新規作成処理
  const saveNewWordRecord = async () => {
    const result = await createWordRecordAction({
      title: data.title,
      recordedDate: data.recordedDate,
      comment: data.comment,
      mediaIds: selectedMediaData.map((media) => media.id),
    });

    afterSaveAction(result, "ことばの記録を作成しました");
  };

  // ことばの記録の更新処理
  const updateWordRecord = async () => {
    if (!initialEditData?.id) {
      toast.error("ことばの記録が見つかりませんでした");
      return;
    }

    const result = await updateWordRecordAction({
      id: initialEditData.id,
      title: data.title,
      recordedDate: data.recordedDate,
      comment: data.comment,
      mediaIds: selectedMediaData.map((media) => media.id),
    });

    afterSaveAction(result, "ことばの記録を更新しました");
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
          saveNewWordRecord();
          break;
        case "editWordRecord":
          updateWordRecord();
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
    removeMedia,
    cancelEdit,
    saveAction,
  };
};
