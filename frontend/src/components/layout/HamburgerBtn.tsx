"use client";

import Image from "next/image";
import { usePathname } from "next/navigation";
import { useState } from "react";

export default function HamburgerBtn({ children }: Readonly<{ children: React.ReactNode }>) {
  // 現在のパスを取得
  const pathname = usePathname();

  // ハンバーガーメニューを開いた時のパスを管理
  const [openPathname, setOpenPathname] = useState<string | null>(null);

  // 現在のパスとハンバーガーメニューが開いているパスが同じかどうかの判定
  const isOpen = openPathname === pathname;

  return (
    <div className="relative @max-md:h-7 @max-md:w-7">
      <div>
        {/* ハンバーガーボタン */}
        {/* ボタンがクリックされたとき、現在のパスが開いているパスと同じならnullにして閉じ、相違している場合そのパスをセットする */}
        <button
          onClick={() => setOpenPathname(isOpen ? null : pathname)}
          className="bg-brown-light border-brown-dark flex h-10 w-10 cursor-pointer flex-col items-center justify-center gap-1.5 rounded-sm border @max-md:absolute @max-md:-top-1.5 @max-md:left-5 @max-md:h-7 @max-md:w-7 @max-md:gap-1"
        >
          <div
            className={`h-px w-5 rounded-xs bg-white transition @max-md:w-4 ${isOpen ? "translate-y-1.75 rotate-45 @max-md:translate-y-1.25" : ""}`}
          ></div>
          <div
            className={`h-px w-5 rounded-xs bg-white transition @max-md:w-4 ${isOpen ? "opacity-0" : ""}`}
          ></div>
          <div
            className={`h-px w-5 rounded-xs bg-white transition @max-md:w-4 ${isOpen ? "-translate-y-1.75 -rotate-45 @max-md:-translate-y-1.25" : ""}`}
          ></div>
        </button>
        {/* ボタン上部のひよこ */}
        <Image
          src="/images/open-piyo.png"
          alt="ひよこ"
          width={34}
          height={37}
          className={`absolute -top-8.25 -right-1.5 opacity-0 @max-md:-top-3.25 @max-md:-right-5 @max-md:w-5.5 @max-md:-scale-x-100 ${isOpen ? "opacity-100" : ""}`}
        />
      </div>
      <div hidden={!isOpen}>{children}</div>
    </div>
  );
}
