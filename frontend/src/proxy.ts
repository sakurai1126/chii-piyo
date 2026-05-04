import { NextResponse, type NextRequest } from "next/server";

import { verifyIdToken } from "@/lib/auth/verify-jwt";

// ログインページなどの公開パス
const PUBLIC_PATHS = ["/login"];

/**
 * IDトークンの検証を行うproxy
 * 署名・Issuer・Audience・有効期限を全て検証する
 */
export const proxy = async (request: NextRequest) => {
  const { pathname } = request.nextUrl;

  // 公開パスの場合はスキップする
  if (PUBLIC_PATHS.some((p) => pathname.startsWith(p))) return NextResponse.next();

  // IDトークンCookieの取得
  const idToken = request.cookies.get("id_token")?.value;

  // IDトークンがない場合はログインページにリダイレクト
  if (!idToken) return redirectToLogin(request);

  // IDトークンを検証
  const verified = await verifyIdToken(idToken);

  // 検証失敗時はログインページにリダイレクトしつつCookieを削除する
  if (!verified) {
    const response = redirectToLogin(request);
    response.cookies.delete("id_token");
    response.cookies.delete("refresh_token");
    return response;
  }

  // 検証成功時はリクエストをそのまま続行する
  return NextResponse.next();
};

/**
 * ログインページへのリダイレクトレスポンスを生成する
 */
const redirectToLogin = (request: NextRequest) => {
  const url = request.nextUrl.clone();
  url.pathname = "/login";
  return NextResponse.redirect(url);
};

// 正規表現でproxyを適用するパスを絞込
// _next/static, _next/image, favicon.ico, images配下、その他拡張子を持った静的ファイルは除外する
export const config = {
  matcher: [String.raw`/((?!_next/static|_next/image|favicon.ico|images|.*\..*).*)`],
};
