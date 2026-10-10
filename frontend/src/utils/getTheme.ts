import "server-only";

import { cookies } from "next/headers";

import { getCurrentUser } from "@/features/auth/server";

export const getTheme = async () => {
  const cookieStore = await cookies();
  const theme = cookieStore.get("theme")?.value;

  try {
    const user = await getCurrentUser();

    // Cookieが無い、またはDBと異なる場合はCookieを再セットする
    return {
      isDarkMode: user.isDarkMode,
      needsCookieRestore: theme !== (user.isDarkMode ? "dark" : "light"),
    };
  } catch (error) {
    // 未ログインの場合はDBを参照できないため、Cookieがあればそれを使う
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return {
        isDarkMode: theme === "dark",
        needsCookieRestore: false,
      };
    }
    throw error;
  }
};
