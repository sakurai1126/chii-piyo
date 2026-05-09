"use server";

import { type SharingGroupResponseDto, SharingGroupManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外を直接出さず、成功/失敗を判別可能な形にする
export type GetSharingGroupsActionResult =
  | { success: true; data: SharingGroupResponseDto[] }
  | { success: false; error: string };

/**
 * 共有グループ一覧を取得するサーバーアクション
 *
 * @returns
 * 成功時: 共有グループ配列
 * 失敗時: エラーメッセージ
 */
export const getSharingGroupsAction = async (): Promise<GetSharingGroupsActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、SharingGroupManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new SharingGroupManagementApi(configuration);

    // 共有グループ一覧を取得
    const response = await apiClient.getSharingGroups({
      xRequestedWith: "XMLHttpRequest",
    });

    return { success: true, data: response };
  } catch (error) {
    console.error("getSharingGroupsAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "共有グループの取得に失敗しました" };
  }
};
