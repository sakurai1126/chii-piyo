import "server-only";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

/**
 * DBから現在ログインしているユーザーのロール情報を取得し管理者権限かどうかを判定し返却する
 * サーバーコンポーネントからのみ呼び出し可能
 */
export const isAdminUser = async () => {
  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new UserManagementApi(configuration);

    const currentUser = await apiClient.getMe({
      xRequestedWith: "XMLHttpRequest",
    });

    return currentUser.role === "ADMIN";
  } catch {
    // 未ログインや認証失敗時はfalseを返す
    return false;
  }
};
