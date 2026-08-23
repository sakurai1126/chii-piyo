import type { Metadata, Viewport } from "next";
import { M_PLUS_Rounded_1c, Zen_Maru_Gothic } from "next/font/google";
import "@/styles/globals.css";

import { ThemeCookieSetter } from "@/components/layout/ThemeCookieSetter";
import { Providers } from "@/components/layout/providers";
import { ServiceWorkerRegister } from "@/components/pwa/ServiceWorkerRegister";
import { Toast } from "@/components/ui/Toast";
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

// Viewport の設定
export const viewport: Viewport = {
  themeColor: "#ffe875",
  width: "device-width",
  initialScale: 1,
};

// メタデータ設定
export const metadata: Metadata = {
  title: "ちいぴよ",
  description: "子育て記録・思い出共有アプリ",
  // iOS端末でアプリとしてホーム画面に追加した際の表示設定
  appleWebApp: {
    // ホーム画面に追加した際に、safariのURLバー等を非表示にしアプリ風の見た目にする
    capable: true,
    statusBarStyle: "default",
    title: "ちいぴよ",
  },
  icons: {
    icon: "/icon.png",
    apple: "/icons/apple-touch-icon.png",
  },
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
        <ServiceWorkerRegister />
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
