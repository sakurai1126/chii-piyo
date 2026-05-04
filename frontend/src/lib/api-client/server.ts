import "server-only";

import { getValidIdToken } from "@/lib/auth/refresh";

import { Configuration } from "./gen/runtime";

/**
 * Server ActionsからAPIを呼ぶ際の共通設定を生成する
 * 必要に応じてトークンをリフレッシュする
 */
export const createAuthorizedConfig = async (): Promise<Configuration> => {
  const idToken = await getValidIdToken();

  return new Configuration({
    basePath: process.env.API_BASE_URL,
    accessToken: idToken ?? undefined,
  });
};
