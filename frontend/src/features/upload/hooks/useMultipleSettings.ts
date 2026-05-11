"use client";

import { useId, useRef, useState } from "react";

import { UseSharingGroupsResult } from "@/features/sharing/types";

import { UploadMetadata } from "../types";

type SelectedState = {
  albumId?: number;
  takenAt?: string;
  tagIds?: number[];
  sharingGroupId?: number;
};

type Props = {
  sharingGroupsState: UseSharingGroupsResult;
  updateAllMetadata: (patch: Partial<UploadMetadata>) => void;
};

export const useMultipleSettings = ({ updateAllMetadata, sharingGroupsState }: Props) => {
  const uid = useId();
  // 一括設定の開閉状態
  const [isOpen, setIsOpen] = useState(false);
  // "yyyy-MM-dd"形式で今日の日付を取得
  const today = new Date().toLocaleDateString("sv-SE");

  // 一括設定の選択状態を管理するローカルステート
  const [selected, setSelected] = useState({
    albumId: undefined,
    takenAt: today,
    tagIds: [] as number[],
    sharingGroupId: undefined,
  } as SelectedState);

  // ユーザーへのフィードバックメッセージ
  const [message, setMessage] = useState("");

  // 複数回の変更やリセットで前のタイマーをクリアできるようrefで保持
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  /**
   * 一括で変更を適応する関数
   */
  const handleChange = () => {
    setMessage(""); // 変更前にメッセージをリセット

    // 変更がある項目だけをパッチとして作成
    const patch: Partial<UploadMetadata> = {};
    if (selected.albumId !== undefined) patch.albumId = selected.albumId;
    if (selected.takenAt !== undefined) patch.takenAt = selected.takenAt;
    if (selected.tagIds !== undefined) patch.tagIds = selected.tagIds;
    if (selected.sharingGroupId !== undefined) patch.sharingGroupId = selected.sharingGroupId;

    // Reactの状態を更新する関数を呼ぶことで再レンダリングをトリガー
    updateAllMetadata(patch);

    setMessage("すべてのアップロードファイルに変更を適用しました");

    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => setMessage(""), 5000);
  };

  /**
   * 一括で変更をリセットする関数
   */
  const handleReset = () => {
    const defaultSharingGroupId = sharingGroupsState.sharingGroups[0]?.id;

    setSelected({
      albumId: undefined,
      takenAt: today,
      tagIds: [],
      sharingGroupId: defaultSharingGroupId,
    });

    updateAllMetadata({
      albumId: undefined,
      takenAt: undefined,
      tagIds: [],
      sharingGroupId: defaultSharingGroupId,
    });
    setMessage("すべてのアップロードファイルの設定をリセットしました");

    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => setMessage(""), 5000);
  };
  return { uid, isOpen, setIsOpen, selected, setSelected, message, handleChange, handleReset };
};
