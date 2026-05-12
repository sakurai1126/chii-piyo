"use client";

import { useState } from "react";

import { useUploadMediaState, useUploadRunner } from "@/features/upload";

/**
 * アップロードページ全体の状態管理を提供するフック
 *
 * @returns
 * - items: アップロード対象のアイテムの配列
 * - setImageAndUrl: 画像ファイルを受け取って初期値、プレビューURL、縦横サイズを取得しセットする関数
 * - setVideoAndUrl: 動画ファイルを受け取って初期値、プレビューURL、縦横サイズを取得しセットする関数
 * - removeFile: 指定したインデックスのアイテムを削除し、URLを解放する関数
 * - removeAllFiles: すべてのアイテムを削除し、URLを解放する関数
 * - handleUpload: アップロード処理を実行する関数
 * - isUploading: アップロード処理中かどうかの状態
 * - resultMessage: アップロード結果のメッセージ表示用の状態
 */
export const useUploadPage = () => {
  const {
    items,
    setImageAndUrl,
    setVideoAndUrl,
    removeFile,
    removeAllFiles,
    updateItem,
    updateItemMetadata,
    updateAllMetadata,
  } = useUploadMediaState();

  // アップロード結果のメッセージ表示用
  const [resultMessage, setResultMessage] = useState<string | null>(null);

  // useUploadRunnerフックは状態変化のコールバックで useUploadMediaState 側のstateを更新する
  const { upload, isUploading } = useUploadRunner({
    onItemUpdate: (itemId, state) => {
      updateItem(itemId, state);
    },
    onAllComplete: ({ successCount, failedCount }) => {
      if (failedCount === 0) {
        setResultMessage(`${successCount}件のアップロードが完了しました`);
      } else {
        setResultMessage(
          `${successCount}件成功 / ${failedCount}件失敗しました。失敗したファイルは再度アップロードできます`,
        );
      }
    },
  });

  // アップロード実行処理
  const handleUpload = async () => {
    // アップロード前に結果メッセージをリセット
    setResultMessage(null);

    // failed と idle のみアップロード対象
    const targets = items.filter((item) => item.status === "idle" || item.status === "failed");
    await upload(targets);
  };

  return {
    items,
    setImageAndUrl,
    setVideoAndUrl,
    removeFile,
    removeAllFiles,
    handleUpload,
    isUploading,
    resultMessage,
    updateItemMetadata,
    updateAllMetadata,
  };
};
