"use server";

import { type TagResponseDto, TagManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外を直接出さず、成功/失敗を判別可能な形にする
export type GetTagsActionResult =
  | { success: true; data: TagResponseDto[] }
  | { success: false; error: string };

/**
 * タグ一覧を取得するサーバーアクション
 *
 * @returns
 * 成功時: タグ配列
 * 失敗時: エラーメッセージ
 */
export const getTagsAction = async (): Promise<GetTagsActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TagManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    // タグ一覧を取得
    const response = await apiClient.getTags({
      xRequestedWith: "XMLHttpRequest",
    });

    return { success: true, data: response };
  } catch (error) {
    console.error("getTagsAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "タグの取得に失敗しました" };
  }
};
