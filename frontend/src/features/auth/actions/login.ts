"use server";

import { redirect } from "next/navigation";

import { signIn } from "@/lib/auth/cognito";
import { setAuthCookies } from "@/lib/auth/session";

export type LoginState = { error?: string };

/**
 * ログイン処理
 * フォームから受け取ったメールアドレス・パスワードでCognitoに認証する
 */
export const loginAction = async (
  _prevState: LoginState,
  formData: FormData,
): Promise<LoginState> => {
  const email = formData.get("email") as string;
  const password = formData.get("password") as string;

  if (!email || !password) return { error: "メールアドレスとパスワードを入力してください" };

  try {
    // 認証リクエストを送信してトークンを取得
    const result = await signIn(email, password);
    if (!result.AuthenticationResult) return { error: "認証に失敗しました" };
    const { IdToken, RefreshToken } = result.AuthenticationResult;
    if (!IdToken || !RefreshToken) return { error: "認証情報の取得に失敗しました" };

    // 取得したトークンをクッキーに保存
    await setAuthCookies({
      idToken: IdToken,
      refreshToken: RefreshToken,
    });
  } catch (error) {
    console.error("ログインエラー", error);
    // congnito.tsのhandleCognitoErrorで指定したエラーメッセージを返す
    if (error instanceof Error) return { error: error.message };
    return { error: "認証中にエラーが発生しました" };
  }

  redirect("/");
};
