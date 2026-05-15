"use server";

import { type AlbumResponseDto, AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外を直接出さず、成功/失敗を判別可能な形にする
export type GetAlbumsActionResult =
  | { success: true; data: AlbumResponseDto[] }
  | { success: false; error: string };

/**
 * アルバム一覧を取得するサーバーアクション
 *
 * @returns
 * 成功時: アルバム配列
 * 失敗時: エラーメッセージ
 */
export const getAlbumsAction = async (): Promise<GetAlbumsActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、AlbumManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new AlbumManagementApi(configuration);

    // アルバム一覧を取得
    const response = await apiClient.getAlbums({
      xRequestedWith: "XMLHttpRequest",
    });

    return { success: true, data: response };
  } catch (error) {
    console.error("getAlbumsAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "アルバムの取得に失敗しました" };
  }
};
