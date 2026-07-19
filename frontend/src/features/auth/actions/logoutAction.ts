"use server";

import { redirect } from "next/navigation";

import { clearAuthCookies } from "@/lib/auth/session";

/**
 * ログアウト処理
 * Cookieを削除してログイン画面にリダイレクトする
 */
export const logoutAction = async () => {
  await clearAuthCookies();
  redirect("/login?logout=success");
};
