import "server-only";

import { cache } from "react";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

/**
 * DBから現在ログインしているユーザーの情報を取得する
 * サーバーコンポーネントからのみ呼び出し可能
 * cacheでAPI呼び出しを1回にまとめる
 */
export const getCurrentUser = cache(async () => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new UserManagementApi(configuration);

  return await apiClient.getMe({
    xRequestedWith: "XMLHttpRequest",
  });
});
