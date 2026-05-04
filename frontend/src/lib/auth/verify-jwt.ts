import { jwtVerify, createRemoteJWKSet } from "jose";

const COGNITO_JWKS_URI = process.env.COGNITO_JWKS_URI!;
const COGNITO_ISSUER = process.env.COGNITO_ISSUER!;
const COGNITO_CLIENT_ID = process.env.COGNITO_CLIENT_ID!;

// joseのcreateRemoteJWKSetは内部で公開鍵を自動キャッシュする
// JWKSはトップレベルでキャッシュすることで初回兼商事に1回フェッチされるようにする
const JWKS = createRemoteJWKSet(new URL(COGNITO_JWKS_URI), {
  // 10分間キャッシュする
  // キャッシュ期間を過ぎたら次のリクエストで新しい公開鍵がフェッチされる
  cacheMaxAge: 10 * 60 * 1000,
  // 公開鍵の取得が5秒以内に終わらなければタイムアウトする
  // タイムアウトの場合は検証失敗とみなしログインページにリダイレクトする
  timeoutDuration: 5000,
});

/**
 * CognitoのIDトークンを検証する
 * 署名・Issuer・Audience・有効期限を全てチェックする
 *
 * @param token 検証対象のJWT文字列
 * @returns 検証成功可否の真偽値
 */
export const verifyIdToken = async (token: string): Promise<boolean> => {
  try {
    // joseのjwtVerifyは署名・Issuer・Audience・有効期限を全て検証する
    const { payload } = await jwtVerify(token, JWKS, {
      issuer: COGNITO_ISSUER,
      audience: COGNITO_CLIENT_ID,
    });

    // Cognitoの仕様上、IDトークンには token_use="id" クレームが入る
    // アクセストークンが誤って送られてきた場合の追加防御
    if (payload.token_use !== "id") return false;
    // 最低限のクレームがあるかpayloadの型チェック
    if (typeof payload.sub !== "string" || typeof payload.email !== "string") return false;

    return true;
  } catch {
    // 期限切れ・署名不一致・Issuer不一致など全ての検証失敗はここでキャッチされる
    return false;
  }
};
