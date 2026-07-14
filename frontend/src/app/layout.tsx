import type { Metadata } from "next";
import { M_PLUS_Rounded_1c, Zen_Maru_Gothic } from "next/font/google";
import "@/styles/globals.css";

import ThemeCookieSetter from "@/components/layout/ThemeCookieSetter";
import Providers from "@/components/layout/providers";
import Toast from "@/components/ui/Toast";
import { cn } from "@/utils/cn";
import { getTheme } from "@/utils/getTheme";

// 本文フォントとして使用
const mPlusRounded1c = M_PLUS_Rounded_1c({
  subsets: ["latin"],
  weight: ["300", "400", "500", "700"],
  variable: "--font-m-plus-rounded",
  display: "swap",
  preload: false,
});

// 見出しフォントとして使用
const zenMaruGothic = Zen_Maru_Gothic({
  subsets: ["latin"],
  weight: ["500"],
  variable: "--font-zen-maru-gothic",
  display: "swap",
  preload: false,
});

// アプリ全体のメタデータ
export const metadata: Metadata = {
  title: "Chii-Piyo",
  description: "育児記録管理アプリ",
};

export default async function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  // ダークモードの設定を取得
  // およびCookie再セットの必要（ユーザーログイン済かつCookieがない状態）があるかの判定
  const { isDarkMode, needsCookieRestore } = await getTheme();

  return (
    <html
      lang="ja"
      className={cn(mPlusRounded1c.variable, zenMaruGothic.variable, isDarkMode && "dark")}
    >
      <body>
        {/* ユーザーログイン済かつCookieが消えていた場合、Cookieを再セット */}
        {needsCookieRestore && <ThemeCookieSetter isDarkMode={isDarkMode} />}
        <Providers>
          {children}
          <Toast />
        </Providers>
      </body>
    </html>
  );
}
