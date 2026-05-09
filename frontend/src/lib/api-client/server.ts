import "server-only";

import { getValidIdToken } from "@/lib/auth/refresh";

import { Configuration } from "./gen/runtime";

/**
 * Server ActionsからAPIを呼ぶ際の共通設定を生成する
 * 生成クライアントのConfigurationにaccessTokenを渡し、各APIメソッド呼び出し時に Authorization: Bearer {token} が付与されるようにする
 * 必要に応じてトークンをリフレッシュする
 */
export const createAuthorizedConfig = async (): Promise<Configuration> => {
  const idToken = await getValidIdToken();

  // トークンが取得できない場合は認証エラーとして例外を投げる
  if (!idToken) {
    throw new Error("UNAUTHORIZED");
  }

  return new Configuration({
    basePath: process.env.API_BASE_URL,
    accessToken: idToken,
  });
};
