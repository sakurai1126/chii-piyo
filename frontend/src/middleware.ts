import { decodeJwt } from "jose";
import { NextResponse, type NextRequest } from "next/server";

import { refreshToken as cognitoRefresh } from "@/lib/auth/cognito";
import { isTokenExpiringSoon } from "@/lib/auth/session";
import { verifyIdToken } from "@/lib/auth/verify-jwt";

// ログインページなどの公開パス
const PUBLIC_PATHS = ["/login"];

/**
 * IDトークンの検証を行うmiddleware
 * 署名・Issuer・Audience・有効期限を全て検証する
 */
export const middleware = async (request: NextRequest) => {
  // AmplifyデフォルトURLから本番環境へのリダイレクト
  const host = request.headers.get("host");
  if (host?.endsWith(".amplifyapp.com")) {
    const url = new URL(request.url);
    url.host = "chii-piyo.s-repo.link";
    url.protocol = "https:";
    return NextResponse.redirect(url, 301);
  }

  const { pathname } = request.nextUrl;

  // 公開パスの場合はスキップする
  if (PUBLIC_PATHS.some((p) => pathname.startsWith(p))) {
    return NextResponse.next();
  }

  // IDトークンとリフレッシュトークンの取得
  const idToken = request.cookies.get("id_token")?.value;
  const refreshTokenValue = request.cookies.get("refresh_token")?.value;

  // IDトークン/リフレッシュトークン両方がない場合はログインページにリダイレクト
  if (!idToken && !refreshTokenValue) {
    return redirectToLogin(request);
  }

  // IDトークンを検証
  let isVerified = false;
  if (idToken) {
    isVerified = await verifyIdToken(idToken);
  }

  // IDトークンが有効、かつ期限切れ間近でない場合はそのまま通す
  if (isVerified && !isTokenExpiringSoon(idToken!)) {
    return NextResponse.next();
  }

  // IDトークンが期限切れまたは期限切れ間近で、リフレッシュトークンがある場合
  if (refreshTokenValue && idToken) {
    // リフレッシュ処理
    try {
      // JWTをパースして、中身のデータを取り出し
      const decoded = decodeJwt(idToken);

      // ID(subクレーム)が含まれているか確認
      if (decoded.sub) {
        // Cognitoに対してリフレッシュトークンとユーザーID(sub)を送り新しいトークンを要求
        const result = await cognitoRefresh(refreshTokenValue, decoded.sub);
        const newIdToken = result.AuthenticationResult?.IdToken;

        // リフレッシュ成功
        if (newIdToken) {
          // Server Componentに進む前にCookieを更新する

          // 後続のサーバーコンポーネントが古いトークンを読み取ってしまわないよう、ブラウザからのリクエストのCookieを新しいトークンに書き換え
          request.cookies.set("id_token", newIdToken);
          request.cookies.set("refresh_token", refreshTokenValue);

          // 次の処理に更新したリクエストヘッダーを含めつつ、レスポンスオブジェクトを作成
          const response = NextResponse.next({
            request: { headers: request.headers },
          });

          // ブラウザ側に保存させるためのCookieの設定を定義
          const cookieOptions = {
            httpOnly: true,
            secure: process.env.NODE_ENV === "production",
            sameSite: "lax" as const,
            path: "/",
            maxAge: 60 * 60 * 24 * 30, // 30日
          };

          // 作成したレスポンスに対して、ブラウザに保存させるCookieをセット
          response.cookies.set("id_token", newIdToken, cookieOptions);
          response.cookies.set("refresh_token", refreshTokenValue, cookieOptions);

          // 最終的なレスポンスを返して、リクエストを後続の処理に流す
          return response;
        }
      }
    } catch (error) {
      console.error("リフレッシュ失敗:", error);
    }
  }

  // 検証失敗時はログインページにリダイレクトしつつCookieを削除する
  const response = redirectToLogin(request);
  response.cookies.delete("id_token");
  response.cookies.delete("refresh_token");
  return response;
};

/**
 * ログインページへのリダイレクトレスポンスを生成する
 */
const redirectToLogin = (request: NextRequest) => {
  const url = request.nextUrl.clone();
  url.pathname = "/login";
  return NextResponse.redirect(url);
};

// 正規表現でmiddlewareを適用するパスを絞込
// _next/static, _next/image, favicon.ico, images配下、その他拡張子を持った静的ファイルは除外する
export const config = {
  // AWS SDKを使用するためNode.jsランタイムで実行する
  runtime: "nodejs",
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)"], // NOSONAR Next.jsのmatcherは静的文字列リテラル必須のためString.rawを使えない
};
