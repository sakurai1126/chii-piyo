import "server-only";

import { cookies } from "next/headers";

import { getCurrentUser } from "@/features/auth/server";

export const getTheme = async () => {
  // Cookieからテーマ設定を読み取る
  const cookieStore = await cookies();
  const theme = cookieStore.get("theme")?.value;

  if (theme) {
    return {
      isDarkMode: theme === "dark",
      needsCookieRestore: false,
    };
  }

  // Cookieが存在しない場合、ユーザー情報を取得する
  try {
    const user = await getCurrentUser();

    // ログイン済の場合はCookie再セットのフラグを立てる
    return {
      isDarkMode: user.isDarkMode,
      needsCookieRestore: true,
    };
  } catch (error) {
    // ログインしていない場合はfalseを返す
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return {
        isDarkMode: false,
        needsCookieRestore: false,
      };
    }
    throw error;
  }
};
