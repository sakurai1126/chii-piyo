"use client";

import { useId, useState } from "react";

import { toast } from "@/components/ui/Toast";

import { UploadMetadata } from "../types";

type SelectedState = {
  albumId?: number;
  takenAt?: string;
  tagIds?: number[];
  sharingGroupId?: number;
};

type Props = {
  updateAllMetadata: (patch: Partial<UploadMetadata>) => void;
};

export const useMultipleSettings = ({ updateAllMetadata }: Props) => {
  const uid = useId();
  // 一括設定の開閉状態
  const [isOpen, setIsOpen] = useState(false);
  // "yyyy-MM-dd"形式で今日の日付を取得
  const today = new Date().toLocaleDateString("sv-SE");

  // 一括設定の選択状態を管理するローカルステート
  const [selected, setSelected] = useState<SelectedState>({
    albumId: undefined,
    takenAt: today,
    tagIds: [],
    sharingGroupId: undefined,
  });

  /**
   * 一括で変更を適応する関数
   */
  const handleChange = () => {
    // 変更がある項目だけをパッチとして作成
    const patch: Partial<UploadMetadata> = {};
    if (selected.albumId !== undefined) patch.albumId = selected.albumId;
    if (selected.takenAt !== undefined) patch.takenAt = selected.takenAt;
    if (selected.tagIds !== undefined) patch.tagIds = selected.tagIds;
    if (selected.sharingGroupId !== undefined) patch.sharingGroupId = selected.sharingGroupId;

    // Reactの状態を更新する関数を呼ぶことで再レンダリングをトリガー
    updateAllMetadata(patch);

    toast.success("すべてのアップロードファイルに変更を適用しました");
  };

  /**
   * 一括で変更をリセットする関数
   */
  const handleReset = () => {
    setSelected({
      albumId: undefined,
      takenAt: today,
      tagIds: [],
      sharingGroupId: undefined,
    });

    updateAllMetadata({
      albumId: undefined,
      takenAt: undefined,
      tagIds: [],
      sharingGroupId: undefined,
    });

    toast.success("すべてのアップロードファイルの設定をリセットしました");
  };
  return { uid, isOpen, setIsOpen, selected, setSelected, handleChange, handleReset };
};
