import "server-only";

import { decodeJwt } from "jose";
import { cookies } from "next/headers";

const ID_TOKEN_COOKIE = "id_token";
const REFRESH_TOKEN_COOKIE = "refresh_token";

const COOKIE_OPTIONS = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  // CSRF対策のため、別オリジンからのPOSTリクエストにCookieを付けない
  sameSite: "lax" as const,
  path: "/",
};

// アクセストークン以外の認証情報を管理
// アクセストークンはユーザー情報を持たずDB同期に使用することができないためIDトークンのみを使用する
type Tokens = {
  idToken: string;
  refreshToken: string;
};

/**
 * 認証トークンをHttpOnly Cookieに保存する
 * Server ActionsまたはRoute Handlerからのみ呼び出し可能
 */
export const setAuthCookies = async (tokens: Tokens): Promise<void> => {
  const cookieStore = await cookies();

  // IDトークンとリフレッシュトークンの有効期限を30日間に設定
  const maxAge = 60 * 60 * 24 * 30;

  // IDトークンとリフレッシュトークンをCookieに保存
  cookieStore.set(ID_TOKEN_COOKIE, tokens.idToken, { ...COOKIE_OPTIONS, maxAge });
  cookieStore.set(REFRESH_TOKEN_COOKIE, tokens.refreshToken, { ...COOKIE_OPTIONS, maxAge });
};

/**
 * 現在のIDトークンを取得する
 */
export const getIdToken = async (): Promise<string | undefined> => {
  const cookieStore = await cookies();
  return cookieStore.get(ID_TOKEN_COOKIE)?.value;
};

/**
 * 現在のリフレッシュトークンを取得する
 */
export const getRefreshToken = async (): Promise<string | undefined> => {
  const cookieStore = await cookies();
  return cookieStore.get(REFRESH_TOKEN_COOKIE)?.value;
};

/**
 * 認証トークンを全て削除する
 */
export const clearAuthCookies = async (): Promise<void> => {
  const cookieStore = await cookies();
  cookieStore.delete(ID_TOKEN_COOKIE);
  cookieStore.delete(REFRESH_TOKEN_COOKIE);
};

/**
 * IDトークンが期限切れ間近か確認する
 * 残り5分を切ったら期限切れ間近と判定
 */
export const isTokenExpiringSoon = (idToken: string): boolean => {
  try {
    const decoded = decodeJwt(idToken);
    if (!decoded.exp) throw new Error("JWTにexpクレームがありません");
    const now = Math.floor(Date.now() / 1000);
    return decoded.exp - now < 300;
  } catch {
    return true;
  }
};
