"use server";

import { UserManagementApi, UserResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  displayName?: string;
  s3key?: string;
  isDarkMode?: boolean;
  isEasyMode?: boolean;
};

type ActionResult = { success: true; user: UserResponseDto } | { success: false; error: string };

/**
 * ユーザー情報のデータを更新するサーバーアクション
 *
 * @param input
 * 更新するユーザー情報
 *
 * @returns
 * 成功時：成功フラグ
 * 失敗時：失敗フラグ + エラーメッセージ
 */
export const updateProfileAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、UserManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new UserManagementApi(configuration);

    const response = await apiClient.updateMe({
      xRequestedWith: "XMLHttpRequest",
      userUpdateData: {
        displayName: input.displayName,
        s3key: input.s3key,
        isDarkMode: input.isDarkMode,
        isEasyMode: input.isEasyMode,
      },
    });

    return { success: true, user: response };
  } catch (error) {
    console.error("updateProfileAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "プロフィール更新に失敗しました" };
  }
};
