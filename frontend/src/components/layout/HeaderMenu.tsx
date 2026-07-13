import Image from "next/image";
import Link from "next/link";

import { isAdminUser, isEasyMode } from "@/features/auth";

export const HeaderMenu = async () => {
  const [isAdmin, isEasy] = await Promise.all([isAdminUser(), isEasyMode()]);
  return <>{isEasy ? <EasyModeMenu /> : <NormalModeMenu isAdmin={isAdmin} />}</>;
};

const EasyModeMenu = () => {
  return (
    <div className="bg-background/80 fixed top-0 left-0 -z-1 h-screen w-screen pt-20 backdrop-blur-[15px]">
      <div className="mx-auto grid max-w-80 grid-cols-3 gap-1.5">
        <Link
          href="/"
          className="bg-menu-home-bg border-menu-home-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-1.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">ホーム</p>
        </Link>
        <Link
          href="/media?mediaKind=PHOTO"
          className="bg-menu-image-bg border-menu-image-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-2.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">写真一覧</p>
        </Link>
        <Link
          href="/media?mediaKind=VIDEO"
          className="bg-menu-video-bg border-menu-video-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-3.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">動画一覧</p>
        </Link>
        <Link
          href="/albums"
          className="bg-menu-album-bg border-menu-album-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-4.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">アルバム</p>
        </Link>
        <Link
          href="/favorites"
          className="bg-menu-favorite-bg border-menu-favorite-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-5.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">お気に入り</p>
        </Link>
        <Link
          href="/analysis"
          className="bg-menu-graph-bg border-menu-graph-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-6.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">グラフ</p>
        </Link>
        <Link
          href="/first-records"
          className="bg-menu-first-bg border-menu-first-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-7.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">はじめて</p>
        </Link>
        <Link
          href="/word-records"
          className="bg-menu-word-bg border-menu-word-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-8.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">ことば</p>
        </Link>
        <Link
          href="/settings"
          className="bg-menu-settings-bg border-menu-settings-border flex aspect-square flex-col items-center justify-center gap-1.5 rounded-lg border"
        >
          <Image src="/images/easy-menu-icon-9.svg" alt="" width={34} height={34} />
          <p className="text-black-text text-sm font-medium">設定</p>
        </Link>
      </div>
    </div>
  );
};

const NormalModeMenu = ({ isAdmin }: { isAdmin: boolean }) => {
  return (
    <div className="absolute top-0 right-16 z-50 text-nowrap @max-md:top-12 @max-md:right-auto @max-md:left-0">
      <div className="relative">
        <Image
          src="/images/menu-illust.png"
          width={185}
          height={139}
          alt="ひよこのイラスト"
          className="pointer-events-none absolute bottom-4 left-1.5 z-51 @max-md:right-4 @max-md:bottom-5 @max-md:left-auto @max-md:w-22"
        />
        <div className="border-brown-dark bg-translucent flex gap-15 rounded-lg border-2 py-7 pr-13 pl-20 backdrop-blur-[15px] @max-md:w-70 @max-md:flex-col @max-md:gap-6 @max-md:border @max-md:p-5 @max-md:pb-16">
          <div className="max-md:gap-19.5 @max-md:flex">
            <Link href="/" className="block w-fit font-medium @max-md:text-[13px]">
              ホーム
            </Link>
            <Link
              href="/settings"
              className="mt-5 block w-fit font-medium @max-md:mt-0 @max-md:text-[13px]"
            >
              設定
            </Link>
          </div>
          <div className="flex gap-15 @max-md:gap-10">
            <div>
              <p className="block w-fit font-medium @max-md:text-[13px]">写真/動画</p>
              <div className="mt-4 grid gap-2.5">
                <Link href="/media" className="block w-fit text-sm @max-md:text-xs">
                  写真/動画一覧
                </Link>
                <Link href="/upload" className="block w-fit text-sm @max-md:text-xs">
                  アップロード
                </Link>
                <Link href="/albums" className="block w-fit text-sm @max-md:text-xs">
                  アルバム
                </Link>
                <Link href="/favorites" className="block w-fit text-sm @max-md:text-xs">
                  お気に入り
                </Link>
                {isAdmin && (
                  <Link href="/trash" className="block w-fit text-sm @max-md:text-xs">
                    ゴミ箱
                  </Link>
                )}
              </div>
            </div>
            <div>
              <p className="block w-fit font-medium @max-md:text-[13px]">育児記録</p>
              <div className="mt-4 grid gap-2.5">
                {isAdmin && (
                  <>
                    <Link href="/care" className="block w-fit text-sm @max-md:text-xs">
                      日々の記録
                    </Link>
                    <Link href="/analysis" className="block w-fit text-sm @max-md:text-xs">
                      グラフ
                    </Link>
                  </>
                )}

                <Link href="/first-records" className="block w-fit text-sm @max-md:text-xs">
                  はじめて記録
                </Link>
                <Link href="/word-records" className="block w-fit text-sm @max-md:text-xs">
                  ことばの記録
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
