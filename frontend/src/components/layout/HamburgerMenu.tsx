import Image from "next/image";
import Link from "next/link";

export default function HamburgerMenu() {
  return (
    <div className="absolute top-0 right-16 z-50 text-nowrap max-md:top-12 max-md:right-auto max-md:left-5">
      <div className="relative">
        <Image
          src="/images/menu-illust.png"
          width={185}
          height={139}
          alt="ひよこのイラスト"
          className="pointer-events-none absolute bottom-4 left-1.5 z-51 max-md:right-4 max-md:bottom-5 max-md:left-auto max-md:w-22"
        />
        <div className="border-brown-dark bg-translucent flex gap-15 rounded-lg border-2 py-7 pr-13 pl-20 backdrop-blur-[15px] max-md:w-70 max-md:flex-col max-md:gap-6 max-md:border max-md:p-5 max-md:pb-16">
          <div className="max-md:flex max-md:gap-19.5">
            <Link href="/" className="block w-fit font-medium max-md:text-[13px]">
              ホーム
            </Link>
            <Link
              href="/settings"
              className="mt-5 block w-fit font-medium max-md:mt-0 max-md:text-[13px]"
            >
              設定
            </Link>
          </div>
          <div className="flex gap-15 max-md:gap-10">
            <div>
              <p className="block w-fit font-medium max-md:text-[13px]">写真/動画</p>
              <div className="mt-4 grid gap-2.5">
                <Link href="/media" className="block w-fit text-sm max-md:text-xs">
                  写真/動画一覧
                </Link>
                <Link href="/upload" className="block w-fit text-sm max-md:text-xs">
                  アップロード
                </Link>
                <Link href="/albums" className="block w-fit text-sm max-md:text-xs">
                  アルバム
                </Link>
                <Link href="/favorites" className="block w-fit text-sm max-md:text-xs">
                  お気に入り
                </Link>
                <Link href="/trash" className="block w-fit text-sm max-md:text-xs">
                  ゴミ箱
                </Link>
              </div>
            </div>
            <div>
              <p className="block w-fit font-medium max-md:text-[13px]">育児記録</p>
              <div className="mt-4 grid gap-2.5">
                <Link href="/care" className="block w-fit text-sm max-md:text-xs">
                  日々の記録
                </Link>
                <Link href="/analysis" className="block w-fit text-sm max-md:text-xs">
                  グラフ
                </Link>
                <Link href="/first-records" className="block w-fit text-sm max-md:text-xs">
                  はじめて記録
                </Link>
                <Link href="/word-records" className="block w-fit text-sm max-md:text-xs">
                  ことばの記録
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
