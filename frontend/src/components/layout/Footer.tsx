import Image from "next/image";
import Link from "next/link";

export default function Footer() {
  return (
    <footer className="border-brown-dark mt-40 border-t pt-10 pb-15 max-md:mt-20 max-md:pt-6 max-md:pb-10">
      <Image
        src="/images/logo.png"
        alt="Chii-Piyo"
        width={300}
        height={100}
        className="mx-auto max-md:w-45"
      />
      <nav className="mt-4 flex justify-center gap-12 max-md:gap-7">
        <Link href="/" className="max-md:text-[13px]">
          ホーム
        </Link>
        <Link href="/media" className="max-md:text-[13px]">
          写真・動画
        </Link>
        <Link href="/care" className="max-md:text-[13px]">
          育児記録
        </Link>
        <Link href="/settings" className="max-md:text-[13px]">
          設定
        </Link>
      </nav>
    </footer>
  );
}
