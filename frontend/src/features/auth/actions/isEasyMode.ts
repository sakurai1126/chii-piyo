import "server-only";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

/**
 * DBから現在ログインしているユーザーの設定情報を取得し、かんたんモードかどうかを判定し返却する
 * サーバーコンポーネントからのみ呼び出し可能
 */
export const isEasyMode = async () => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new UserManagementApi(configuration);

  const currentUser = await apiClient.getMe({
    xRequestedWith: "XMLHttpRequest",
  });

  return currentUser.isEasyMode;
};
