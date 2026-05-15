"use server";

import {
  MediaManagementApi,
  MediaUploadStatusRequestDtoUploadStatusEnum,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

type Input = {
  mediaId: number;
  uploadStatus: "COMPLETED" | "FAILED" | "PROCESSING";
};

type ActionResult = { success: true } | { success: false; error: string };

/**
 * メディアのアップロード状態を更新するサーバーアクション
 * S3アップロード成功時は COMPLETED 、失敗時は FAILED を渡して呼び出す
 *
 * @param input
 * 更新に必要な情報
 * - mediaId: 更新対象のメディアID
 * - uploadStatus: 更新後のステータス
 *
 * @return
 * 成功時: { success: true }
 * 失敗時: { success: false, error: "エラーメッセージ" }
 */
export const updateMediaStatusAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    // 文字列から生成型のenumに変換
    let statusEnum: MediaUploadStatusRequestDtoUploadStatusEnum;

    switch (input.uploadStatus) {
      case "COMPLETED":
        statusEnum = MediaUploadStatusRequestDtoUploadStatusEnum.Completed;
        break;
      case "FAILED":
        statusEnum = MediaUploadStatusRequestDtoUploadStatusEnum.Failed;
        break;
      default:
        statusEnum = MediaUploadStatusRequestDtoUploadStatusEnum.Processing;
    }

    // 更新APIを呼び出してステータスを更新
    await apiClient.updateMediaUploadStatus({
      xRequestedWith: "XMLHttpRequest",
      id: input.mediaId,
      mediaUpdateStatusData: {
        uploadStatus: statusEnum,
      },
    });

    return { success: true };
  } catch (error) {
    console.error("updateMediaStatusAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "ステータス更新に失敗しました" };
  }
};
