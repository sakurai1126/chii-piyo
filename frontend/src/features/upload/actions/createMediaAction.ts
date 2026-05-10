"use server";

import {
  type MediaUploadRequestDto,
  type MediaUploadResponseDto,
  MediaManagementApi,
  MediaUploadRequestDtoMediaTypeEnum,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult =
  | { success: true; data: MediaUploadResponseDto }
  | { success: false; error: string };

// クライアントから受け取る入力型
type Input = {
  mediaType: "PHOTO" | "VIDEO";
  originalFilename: string;
  contentType: string;
  fileSize: number;
  width?: number;
  height?: number;
  takenAt?: string;
  albumId?: number;
  sharingGroupId: number;
};

/**
 * メディアのメタデータを登録し、署名付きアップロードURLを取得するサーバーアクション
 *
 * @param input
 * アップロードメタデータ
 *
 * @returns
 * 成功時：メディアID + 署名付きURL
 * 失敗時：エラーメッセージ
 */
export const createMediaAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    // 生成型に揃えてリクエストボディを構築
    const requestDto: MediaUploadRequestDto = {
      mediaType:
        input.mediaType === "PHOTO"
          ? MediaUploadRequestDtoMediaTypeEnum.Photo
          : MediaUploadRequestDtoMediaTypeEnum.Video,
      originalFilename: input.originalFilename,
      contentType: input.contentType,
      fileSize: input.fileSize,
      width: input.width ?? null,
      height: input.height ?? null,
      takenAt: input.takenAt ? new Date(input.takenAt) : null,
      albumId: input.albumId ?? null,
      sharingGroupId: input.sharingGroupId,
    };

    // APIを呼び出してメタデータ登録と署名付きURLの取得を行う
    const response = await apiClient.createMedia({
      xRequestedWith: "XMLHttpRequest",
      mediaUploadData: requestDto,
    });

    return { success: true, data: response };
  } catch (error) {
    console.error("createMediaAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディア登録に失敗しました" };
  }
};
