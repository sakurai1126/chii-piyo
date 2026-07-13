"use client";

import Image from "next/image";
import Link from "next/link";

type Props = {
  isAdmin: boolean;
  isEasy: boolean;
};

export const BottomNavigation = ({ isAdmin, isEasy }: Readonly<Props>) => {
  return (
    <nav className="fixed bottom-0 z-10 grid h-16 w-full grid-cols-5 bg-[#FFFFEF] shadow-[0_-4px_10px_rgba(21,12,0,0.1)] transition-all duration-400 md:hidden">
      {isEasy ? <EasyModeMenu /> : <NormalModeMenu isAdmin={isAdmin} />}
    </nav>
  );
};

const EasyModeMenu = () => {
  return (
    <>
      <Link href="/media?mediaKind=PHOTO" className="grid place-content-center">
        <Image src="/images/nav-icon-2.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[11px] font-medium">写真一覧</p>
      </Link>
      <Link href="/media?mediaKind=VIDEO" className="grid place-content-center">
        <Image src="/images/nav-icon-6.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[11px] font-medium">動画一覧</p>
      </Link>
      <Link href="/albums" className="grid place-content-center">
        <Image src="/images/nav-icon-7.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[11px] font-medium">アルバム</p>
      </Link>
      <Link href="/analysis" className="grid place-content-center">
        <Image src="/images/nav-icon-3.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[11px] font-medium">記録</p>
      </Link>
      <Link href="/upload" className="grid place-content-center">
        <Image src="/images/nav-icon-4.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[11px] font-medium">アップロード</p>
      </Link>
    </>
  );
};

const NormalModeMenu = ({ isAdmin }: { isAdmin: boolean }) => {
  return (
    <>
      <Link href="/" className="grid place-content-center">
        <Image src="/images/nav-icon-1.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">ホーム</p>
      </Link>

      <Link href="/media" className="grid place-content-center">
        <Image src="/images/nav-icon-2.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">写真/動画</p>
      </Link>

      <Link href={isAdmin ? "/care" : "/analysis"} className="grid place-content-center">
        <Image src="/images/nav-icon-3.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">記録</p>
      </Link>
      <Link href="/upload" className="grid place-content-center">
        <Image src="/images/nav-icon-4.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">アップロード</p>
      </Link>
      <Link href="/settings" className="grid place-content-center">
        <Image src="/images/nav-icon-5.svg" alt="" width={44} height={44} className="mx-auto" />
        <p className="text-brown-dark -mt-1 text-center text-[10px] font-medium">設定</p>
      </Link>
    </>
  );
};
