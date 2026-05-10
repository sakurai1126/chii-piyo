import { useState } from "react";

import { createAlbumAction } from "../actions/createAlbumAction";

type Options = {
  onSuccess?: () => void;
};

/**
 * アルバム作成用のカスタムフック
 *
 * @returns
 * - createAlbum: アルバム作成関数
 * - isCreating: アルバム作成中かどうかの状態
 * - error: アルバム作成中に発生したエラーメッセージ (成功時はnull)
 */
export const useCreateAlbum = ({ onSuccess }: Options = {}) => {
  // ボタンの二重押下防止に使用するアップロード中かどうかのステータス
  const [isCreating, setIsCreating] = useState(false);

  // アルバム作成中に発生したエラーメッセージを保持
  const [error, setError] = useState<string | null>(null);

  /**
   * アルバムを作成する関数
   *
   * @param title 追加するアルバム名
   */
  const createAlbum = async (title: string) => {
    // すでに作成処理が走っている場合は何もしない
    if (isCreating) return;

    // アルバム名が空の場合はエラーにする
    if (!title.trim()) {
      setError("アルバム名を入力してください");
      return;
    }

    // アルバム作成処理開始前に状態をリセット
    setIsCreating(true);
    setError(null);

    try {
      const result = await createAlbumAction({ title });
      if (!result.success) {
        setError(result.error);
        return;
      }

      // 成功時のコールバックを呼び出す
      onSuccess?.();
    } catch (error) {
      console.error("アルバム作成失敗", error);
      setError("アルバム作成に失敗しました");
    } finally {
      setIsCreating(false);
    }
  };
  return {
    createAlbum,
    isCreating,
    error,
  };
};
