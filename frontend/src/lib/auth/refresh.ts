import "server-only";

import { decodeJwt } from "jose";

import { refreshToken as cognitoRefresh } from "./cognito";
import { getIdToken, getRefreshToken, setAuthCookies, isTokenExpiringSoon } from "./session";

/**
 * 期限切れ間近の場合のみトークンをリフレッシュし、最新のIDトークンを返す
 * IDトークンがない場合やリフレッシュに失敗した場合はnullを返す
 *
 * @returns 有効なIDトークン、またははnull
 */
export const getValidIdToken = async (): Promise<string | null> => {
  const idToken = await getIdToken();

  // IDトークンがない場合はnullを返す（未ログイン状態想定）
  if (!idToken) return null;

  // トークンが期限切れ間近でなければそのまま返す
  if (!isTokenExpiringSoon(idToken)) return idToken;

  // トークンが期限切れ間近の場合はリフレッシュトークンを使用して新しいトークンを取得
  const refreshTokenValue = await getRefreshToken();
  if (!refreshTokenValue) return null;

  try {
    const decoded = decodeJwt(idToken);
    if (!decoded.sub) throw new Error("JWTにsubクレームがありません");

    const result = await cognitoRefresh(refreshTokenValue, decoded.sub);

    const IdToken = result.AuthenticationResult?.IdToken;
    if (!IdToken) return null;

    // RefreshTokenはリフレッシュ時には返されないので既存を維持しつつIDトークンを更新する
    await setAuthCookies({
      idToken: IdToken,
      refreshToken: refreshTokenValue,
    });

    return IdToken;
  } catch (error) {
    console.error("トークンリフレッシュ失敗", error);
    return null;
  }
};
