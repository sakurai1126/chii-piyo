import { useState } from "react";

import { useUploadImagesState, useUploadRunner, type UploadMetadata } from "@/features/upload";

// 共有範囲のデフォルト値
// 一旦初期登録値のグループID=1で固定
const DEFAULT_SHARING_GROUP_ID = 1;

/**
 * アップロードページ全体の状態管理を提供するフック
 *
 * @returns
 * - items: アップロード対象のアイテムの配列
 * - setFileAndUrl: ファイルを受け取って初期値、プレビューURL、縦横サイズを取得しセットする関数
 * - removeFile: 指定したインデックスのアイテムを削除し、URLを解放する関数
 * - removeAllFiles: すべてのアイテムを削除し、URLを解放する関数
 * - handleUpload: アップロード処理を実行する関数
 * - isUploading: アップロード処理中かどうかの状態
 * - resultMessage: アップロード結果のメッセージ表示用の状態
 */
export const useUploadPage = () => {
  const { items, setFileAndUrl, removeFile, removeAllFiles, updateItem } = useUploadImagesState();

  // アップロード結果のメッセージ表示用
  const [resultMessage, setResultMessage] = useState<string | null>(null);

  // useUploadRunnerフックは状態変化のコールバックで useUploadImagesState 側のstateを更新する
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

    // メタデータは現状一括設定の固定値のみ
    const metadata: UploadMetadata = {
      sharingGroupId: DEFAULT_SHARING_GROUP_ID,
    };

    // failed と idle のみアップロード対象
    const targets = items.filter((item) => item.status === "idle" || item.status === "failed");
    await upload(targets, metadata);
  };

  return {
    items,
    setFileAndUrl,
    removeFile,
    removeAllFiles,
    handleUpload,
    isUploading,
    resultMessage,
  };
};
