import Link from "next/link";
import Image from "next/image";

export default function HamburgerMenu() {
  return (
    <div className="absolute right-16 top-0 text-nowrap">
      <div className="relative">
        <Image
          src="/images/menu-illust.png"
          width={143}
          height={95}
          alt="ひよこのイラスト"
          className="absolute bottom-7 left-3 mix-blend-multiply z-10"
        />
        <div className="flex gap-15 bg-white/50 backdrop-blur-[7.5px] py-7 pl-20 pr-13 border-2 border-brown-dark rounded-lg">
          <div>
            <Link href="/" className="block w-fit font-medium">
              ホーム
            </Link>
            <Link href="/settings" className="block w-fit font-medium mt-5">
              設定
            </Link>
          </div>
          <div className="">
            <p className="block w-fit font-medium">写真/動画</p>
            <div className="grid gap-2.5 mt-4">
              <Link href="/media" className="block w-fit text-sm">
                写真/動画一覧
              </Link>
              <Link href="/media/upload" className="block w-fit text-sm">
                アップロード
              </Link>
              <Link href="/albums" className="block w-fit text-sm">
                アルバム
              </Link>
              <Link href="/favorites" className="block w-fit text-sm">
                お気に入り
              </Link>
              <Link href="/trash" className="block w-fit text-sm">
                ゴミ箱
              </Link>
            </div>
          </div>
          <div>
            <p className="block w-fit font-medium">育児記録</p>
            <div className="grid gap-2.5 mt-4">
              <Link href="/care" className="block w-fit text-sm">
                日々の記録
              </Link>
              <Link href="/analysis" className="block w-fit text-sm">
                グラフ
              </Link>
              <Link href="/first-records" className="block w-fit text-sm">
                はじめて記録
              </Link>
              <Link href="/word-records" className="block w-fit text-sm">
                ことばの記録
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
