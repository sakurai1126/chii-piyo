"use server";

import {
  GetMediaListMediaKindEnum,
  type MediaListResponseDto,
  MediaManagementApi,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外を直接出さず、成功/失敗を判別可能な形にする
export type GetMediaListActionResult =
  | { success: true; data: MediaListResponseDto }
  | { success: false; error: string };

type Props = {
  offset?: number; // 取得開始位置
  limit?: number; // 取得件数
  mediaKind?: GetMediaListMediaKindEnum; // "photo" or "video"
  albumId?: number; // アルバムID
  tagId?: number; // タグID
  sharingGroupId?: number; // 共有グループID
  startDate?: Date; // 取得開始日時
  endDate?: Date; // 取得終了日時
};

/**
 * メディア一覧を取得するサーバーアクション
 *
 * @returns
 * 成功時: メディア一覧
 * 失敗時: エラーメッセージ
 */
export const getMediaListAction = async ({
  offset,
  limit,
  mediaKind,
  albumId,
  tagId,
  sharingGroupId,
  startDate,
  endDate,
}: Props = {}): Promise<GetMediaListActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    // メディア一覧を取得
    const response = await apiClient.getMediaList({
      xRequestedWith: "XMLHttpRequest",
      offset,
      limit,
      mediaKind,
      albumId,
      tagId,
      sharingGroupId,
      startDate,
      endDate,
    });

    return { success: true, data: response };
  } catch (error) {
    console.error("getMediaListAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディアの取得に失敗しました" };
  }
};
