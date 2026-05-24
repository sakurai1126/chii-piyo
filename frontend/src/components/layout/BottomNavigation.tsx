"use client";

import Image from "next/image";
import Link from "next/link";

import { useIsBottomScroll } from "@/hooks/useIsBottomScroll";

export default function BottomNavigation() {
  const isBottom = useIsBottomScroll();

  return (
    <nav
      className={`fixed bottom-0 z-10 grid h-16 w-full grid-cols-5 bg-[#FFFFEF] shadow-[0_-4px_10px_rgba(21,12,0,0.1)] transition-all duration-400 md:hidden ${isBottom ? "translate-y-full" : "translate-y-0"}`}
    >
      <Link href="/" className="grid place-content-center">
        <Image
          src="/images/nav-icon-1.svg"
          alt="ホームアイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">ホーム</p>
      </Link>
      <Link href="/media" className="grid place-content-center">
        <Image
          src="/images/nav-icon-2.svg"
          alt="写真/動画アイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">写真/動画</p>
      </Link>
      <Link href="/care" className="grid place-content-center">
        <Image
          src="/images/nav-icon-3.svg"
          alt="記録アイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">記録</p>
      </Link>
      <Link href="/upload" className="grid place-content-center">
        <Image
          src="/images/nav-icon-4.svg"
          alt="アップロードアイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">アップロード</p>
      </Link>
      <Link href="/settings" className="grid place-content-center">
        <Image
          src="/images/nav-icon-5.svg"
          alt="設定アイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">設定</p>
      </Link>
    </nav>
  );
}
