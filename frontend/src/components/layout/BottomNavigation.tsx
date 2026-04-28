"use client";

import { useIsBottomScroll } from "@/hooks/useIsBottomScroll";
import Image from "next/image";
import Link from "next/link";

export default function BottomNavigation() {
  const isBottom = useIsBottomScroll();

  return (
    <nav
      className={`grid grid-cols-5 fixed bottom-0 w-full bg-[#FFFFEF] shadow-[0_-4px_10px_rgba(21,12,0,0.1)] z-10 h-16 md:hidden transition-all duration-400 ${isBottom ? "translate-y-full" : "translate-y-0"}`}
    >
      <Link href="/" className="grid place-content-center">
        <Image
          src="/images/nav-icon-1.svg"
          alt="ホームアイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark font-medium text-[10px] text-center -mt-1">ホーム</p>
      </Link>
      <Link href="/media" className="grid place-content-center">
        <Image
          src="/images/nav-icon-2.svg"
          alt="写真/動画アイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark font-medium text-[10px] text-center -mt-1">写真/動画</p>
      </Link>
      <Link href="/care" className="grid place-content-center">
        <Image
          src="/images/nav-icon-3.svg"
          alt="記録アイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark font-medium text-[10px] text-center -mt-1">記録</p>
      </Link>
      <Link href="/media/upload" className="grid place-content-center">
        <Image
          src="/images/nav-icon-4.svg"
          alt="アップロードアイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark font-medium text-[10px] text-center -mt-1">アップロード</p>
      </Link>
      <Link href="/settings" className="grid place-content-center">
        <Image
          src="/images/nav-icon-5.svg"
          alt="設定アイコン"
          width={44}
          height={44}
          className="mx-auto"
        />
        <p className="text-brown-dark font-medium text-[10px] text-center -mt-1">設定</p>
      </Link>
    </nav>
  );
}
