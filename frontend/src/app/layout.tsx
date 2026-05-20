import type { Metadata } from "next";
import { M_PLUS_Rounded_1c, Zen_Maru_Gothic } from "next/font/google";

import "@/styles/globals.css";
import Providers from "@/components/layout/providers";
import Toast from "@/components/ui/Toast";

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

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="ja" className={`${mPlusRounded1c.variable} ${zenMaruGothic.variable}`}>
      <body>
        <Providers>
          {children}
          <Toast />
        </Providers>
      </body>
    </html>
  );
}
