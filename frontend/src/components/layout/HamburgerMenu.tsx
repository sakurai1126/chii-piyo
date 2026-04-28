import Link from "next/link";
import Image from "next/image";

export default function HamburgerMenu() {
  return (
    <div className="absolute right-16 top-0 text-nowrap max-md:right-auto max-md:left-5 max-md:top-12">
      <div className="relative">
        <Image
          src="/images/menu-illust.png"
          width={143}
          height={95}
          alt="ひよこのイラスト"
          className="absolute bottom-7 left-3 mix-blend-multiply z-1 max-md:w-22 max-md:left-auto max-md:right-4 max-md:bottom-5"
        />
        <div className="flex gap-15 bg-white/50 backdrop-blur-[7.5px] py-7 pl-20 pr-13 border-2 border-brown-dark rounded-lg max-md:border max-md:flex-col max-md:gap-6 max-md:p-5 max-md:w-70 max-md:pb-16">
          <div className="max-md:flex max-md:gap-19.5">
            <Link href="/" className="block w-fit font-medium max-md:text-[13px]">
              ホーム
            </Link>
            <Link
              href="/settings"
              className="block w-fit font-medium mt-5 max-md:text-[13px] max-md:mt-0"
            >
              設定
            </Link>
          </div>
          <div className="flex gap-15 max-md:gap-10">
            <div>
              <p className="block w-fit font-medium max-md:text-[13px]">写真/動画</p>
              <div className="grid gap-2.5 mt-4">
                <Link href="/media" className="block w-fit text-sm max-md:text-xs">
                  写真/動画一覧
                </Link>
                <Link href="/media/upload" className="block w-fit text-sm max-md:text-xs">
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
              <div className="grid gap-2.5 mt-4">
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
