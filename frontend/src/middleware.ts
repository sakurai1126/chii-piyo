import { decodeJwt } from "jose";
import { NextResponse, type NextRequest } from "next/server";

import { refreshToken as cognitoRefresh } from "@/lib/auth/cognito";
import { isTokenExpiringSoon } from "@/lib/auth/session";
import { verifyIdToken } from "@/lib/auth/verify-jwt";

// ログインページなどの公開パス
const PUBLIC_PATHS = ["/login"];

/**
 * ページ内で読み込みを許可する要素通知と認証処理のミドルウェア
 */
export const middleware = async (request: NextRequest) => {
  // Amplifyデフォルトドメインへのアクセスをカスタムドメインへリダイレクトする
  // ボディを返さないためCSPの付与対象外とし、nonce生成前に早期リターンする
  const domainRedirect = redirectToCustomDomain(request);
  if (domainRedirect) return domainRedirect;

  // 自分が出力したスクリプトを識別するためのランダム値
  const nonce = crypto.randomUUID();

  // コンテンツセキュリティポリシーの許可リスト生成
  const csp = [
    // 規定値
    // 自身のオリジンのみ許可
    "default-src 'self'",
    // 実行を許可するスクリプト
    // nonceで自身が出力したものだけを許可し、strict-dynamicによってそこから読み込まれるJSファイルも許可
    // unsafe-evalは文字列をコードとして実行する処理の許可であり、開発サーバーの自動更新が使用するため開発環境に限定
    `script-src 'self' 'nonce-${nonce}' 'strict-dynamic'${process.env.NODE_ENV !== "production" ? " 'unsafe-eval'" : ""}`,
    // 適用を許可するスタイル
    // Next.jsが挿入するインラインスタイルにnonceが付かないため'unsafe-inline'を許可する
    "style-src 'self' 'unsafe-inline'",
    // 表示を許可する画像
    `img-src 'self' blob: data: ${process.env.S3_ORIGIN ?? ""}`,
    // 再生を許可する動画・音声
    `media-src 'self' blob: ${process.env.S3_ORIGIN ?? ""}`,
    // 通信を許可する接続先
    // S3への署名付きURLでの直接アップロードを許可する
    `connect-src 'self' ${process.env.S3_ORIGIN ?? ""}`,
    // 生成を許可する別スレッド実行用Worker
    // blobは画像圧縮ライブラリの画像リサイズ用
    "worker-src 'self' blob:",
    // PDF等の外部ファイルを埋め込むobject/embed要素を禁止
    "object-src 'none'",
    // フォームの送信先を自オリジンに限定する
    "form-action 'self'",
    // 他サイトからのframe埋め込みを禁止し、クリックジャッキングを防ぐ
    "frame-ancestors 'none'",
    // http指定のリソースをhttpsに置き換えて読み込む
    "upgrade-insecure-requests",
  ].join("; ");

  // Next.js自身のスクリプトがブロックされ画面が表示されないのを防ぐためリクエストヘッダーにCSPを付与
  const requestHeaders = new Headers(request.headers);
  requestHeaders.set("x-nonce", nonce);
  requestHeaders.set("Content-Security-Policy", csp);

  // 認証処理
  const response = await handleAuth(request, requestHeaders);

  // ブラウザへ許可リストを通知
  response.headers.set("Content-Security-Policy", csp);

  return response;
};

/**
 * IDトークンの検証を行う
 * 署名・Issuer・Audience・有効期限を全て検証する
 */
const handleAuth = async (request: NextRequest, requestHeaders: Headers) => {
  const { pathname } = request.nextUrl;

  // 公開パスの場合はスキップする
  if (PUBLIC_PATHS.some((p) => pathname.startsWith(p))) {
    return NextResponse.next({ request: { headers: requestHeaders } });
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
    return NextResponse.next({ request: { headers: requestHeaders } });
  }

  // IDトークンが期限切れまたは期限切れ間近で、リフレッシュトークンがある場合
  if (refreshTokenValue && idToken) {
    const refreshed = await refreshSession(request, requestHeaders, idToken, refreshTokenValue);
    if (refreshed) return refreshed;
  }

  // 検証失敗時はログインページにリダイレクトしつつCookieを削除する
  const response = redirectToLogin(request);
  response.cookies.delete("id_token");
  response.cookies.delete("refresh_token");
  return response;
};

/**
 * Amplifyデフォルトドメインへのアクセスをカスタムドメインへリダイレクトする
 * 対象外の場合はnullを返す
 */
const redirectToCustomDomain = (request: NextRequest) => {
  const host = request.headers.get("host");
  if (host !== "main.dnakxm0y9trz3.amplifyapp.com") return null;

  const url = new URL(request.url);
  url.protocol = "https:";
  url.host = "chii-piyo.s-repo.link";
  url.port = "";
  return NextResponse.redirect(url, 301);
};

/**
 * ログインページへのリダイレクトレスポンスを生成する
 */
const redirectToLogin = (request: NextRequest) => {
  const url = request.nextUrl.clone();
  url.pathname = "/login";
  return NextResponse.redirect(url);
};

/**
 * リフレッシュトークンを用いてIDトークンを再取得し、Cookieを更新したレスポンスを返す
 * リフレッシュできない場合はnullを返す
 */
const refreshSession = async (
  request: NextRequest,
  requestHeaders: Headers,
  idToken: string,
  refreshTokenValue: string,
) => {
  try {
    // JWTをパースして、中身のデータを取り出し
    const decoded = decodeJwt(idToken);
    if (!decoded.sub) return null;

    // Cognitoに対してリフレッシュトークンとユーザーID(sub)を送り新しいトークンを要求
    const result = await cognitoRefresh(refreshTokenValue, decoded.sub);
    const newIdToken = result.AuthenticationResult?.IdToken;
    if (!newIdToken) return null;

    // 後続のサーバーコンポーネントが古いトークンを読み取ってしまわないよう、ブラウザからのリクエストのCookieを新しいトークンに書き換え
    request.cookies.set("id_token", newIdToken);
    request.cookies.set("refresh_token", refreshTokenValue);

    // 次の処理に更新したリクエストヘッダーを含めつつ、レスポンスオブジェクトを作成
    const response = NextResponse.next({
      request: { headers: requestHeaders },
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

    return response;
  } catch (error) {
    console.error("リフレッシュ失敗:", error);
    return null;
  }
};

// 正規表現でmiddlewareを適用するパスを絞込
// _next/static, _next/image, favicon.ico, images配下、その他拡張子を持った静的ファイルは除外する
export const config = {
  // AWS SDKを使用するためNode.jsランタイムで実行する
  runtime: "nodejs",
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)"], // NOSONAR Next.jsのmatcherは静的文字列リテラル必須のためString.rawを使えない
};
