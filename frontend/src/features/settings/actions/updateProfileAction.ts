"use server";

import { revalidatePath } from "next/cache";
import { cookies } from "next/headers";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  displayName?: string;
  s3key?: string;
  isDarkMode?: boolean;
  isEasyMode?: boolean;
};

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

    await apiClient.updateMe({
      xRequestedWith: "XMLHttpRequest",
      userUpdateData: {
        displayName: input.displayName,
        s3key: input.s3key,
        isDarkMode: input.isDarkMode,
        isEasyMode: input.isEasyMode,
      },
    });

    // 再レンダリング時、テーマ変更が即時反映されない不整合が起きないようCookieをセット
    if (input.isDarkMode !== undefined) {
      const cookieStore = await cookies();
      cookieStore.set("theme", input.isDarkMode ? "dark" : "light", {
        path: "/",
        maxAge: 604800,
        sameSite: "lax",
        secure: true,
      });
    }

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/settings");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "プロフィール更新に失敗しました");
  }
};
