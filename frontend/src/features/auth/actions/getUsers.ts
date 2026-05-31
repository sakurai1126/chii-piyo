import "server-only";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

/**
 * DBからユーザーの一覧情報を取得する
 * サーバーコンポーネントからのみ呼び出し可能
 */
export const getUsers = async () => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new UserManagementApi(configuration);

  return await apiClient.getUsers({
    xRequestedWith: "XMLHttpRequest",
  });
};
