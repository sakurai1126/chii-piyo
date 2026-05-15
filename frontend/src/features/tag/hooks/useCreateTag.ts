"use client";
import { useState } from "react";

import { createTagAction } from "../actions/createTagAction";

type Options = {
  onSuccess?: () => void;
};

/**
 * タグ作成用のカスタムフック
 *
 * @returns
 * - createTag: タグ作成関数
 * - isCreating: タグ作成中かどうかの状態
 * - error: タグ作成中に発生したエラーメッセージ (成功時はnull)
 */
export const useCreateTag = ({ onSuccess }: Options = {}) => {
  // ボタンの二重押下防止に使用するアップロード中かどうかのステータス
  const [isCreating, setIsCreating] = useState(false);

  // タグ作成中に発生したエラーメッセージを保持
  const [error, setError] = useState<string | null>(null);

  /**
   * タグを作成する関数
   *
   * @param name 追加するタグ名
   */
  const createTag = async (name: string) => {
    // すでに作成処理が走っている場合は何もしない
    if (isCreating) return;

    // タグ名が空の場合はエラーにする
    if (!name.trim()) {
      setError("タグ名を入力してください");
      return;
    }

    // タグ作成処理開始前に状態をリセット
    setIsCreating(true);
    setError(null);

    try {
      const result = await createTagAction({ name });
      if (!result.success) {
        setError(result.error);
        return;
      }

      // 成功時のコールバックを呼び出す
      onSuccess?.();
    } catch (error) {
      console.error("タグ作成失敗", error);
      setError("タグ作成に失敗しました");
    } finally {
      setIsCreating(false);
    }
  };
  return {
    createTag,
    isCreating,
    error,
  };
};
