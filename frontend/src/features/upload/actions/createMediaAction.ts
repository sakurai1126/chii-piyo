"use server";

import { createMediaCommentAction } from "@/features/comment/actions/createCommentAction";
import { updateMediaTagsAction } from "@/features/tag/actions/updateMediaTagsAction";
import {
  type MediaUploadRequestDto,
  type MediaUploadResponseDto,
  MediaManagementApi,
  MediaUploadRequestDtoMediaTypeEnum,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

type ResponseData = MediaUploadResponseDto & {
  warnings?: string[];
};

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
  sharingGroupId?: number;
  tagIds?: number[];
  comment?: string;
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
export const createMediaAction = async (input: Input): Promise<ActionResult<ResponseData>> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    const mediaTypeMap = {
      PHOTO: MediaUploadRequestDtoMediaTypeEnum.Photo,
      VIDEO: MediaUploadRequestDtoMediaTypeEnum.Video,
    } as const;

    // 生成型に揃えてリクエストボディを構築
    const requestDto: MediaUploadRequestDto = {
      mediaType: mediaTypeMap[input.mediaType],
      originalFilename: input.originalFilename,
      contentType: input.contentType,
      fileSize: input.fileSize,
      width: input.width ?? null,
      height: input.height ?? null,
      takenAt: input.takenAt ? new Date(input.takenAt) : null,
      albumId: input.albumId ?? null,
      sharingGroupId: input.sharingGroupId ?? null,
    };

    // APIを呼び出してメタデータ登録と署名付きURLの取得を行う
    const response = await apiClient.createMedia({
      xRequestedWith: "XMLHttpRequest",
      mediaUploadData: requestDto,
    });

    const warnings: string[] = [];

    // コメントがある場合登録する
    if (input.comment) {
      const commentResponse = await createMediaCommentAction({
        mediaId: response.mediaId,
        content: input.comment,
      });

      if (!commentResponse.success) {
        console.warn("コメントの登録に失敗しました", commentResponse.error);
        warnings.push("コメントの登録に失敗しました");
      }
    }

    // タグがある場合登録する
    if (input.tagIds && input.tagIds.length > 0) {
      const tagResponse = await updateMediaTagsAction({
        mediaId: response.mediaId,
        tagIds: input.tagIds,
      });

      if (!tagResponse.success) {
        console.warn("タグの登録に失敗しました", tagResponse.error);
        warnings.push("タグの登録に失敗しました");
      }
    }

    return {
      success: true,
      data: {
        ...response,
        warnings: warnings.length > 0 ? warnings : undefined,
      },
    };
  } catch (error) {
    return handleActionError(error, "メディア登録に失敗しました");
  }
};
