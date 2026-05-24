// src/features/auth/actions/me.ts (例)
import "server-only";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

/**
 * DBから現在ログインしているユーザーの情報を取得する
 * （Server Component または Server Action からのみ呼び出し可能）
 */
export const getCurrentUser = async () => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new UserManagementApi(configuration);

  return await apiClient.getMe({
    xRequestedWith: "XMLHttpRequest",
  });
};
