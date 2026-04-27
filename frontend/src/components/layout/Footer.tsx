import Image from "next/image";
import Link from "next/link";

export default function Footer() {
  return (
    <footer className="mt-40 border-t border-brown-dark pt-10 pb-15">
      <Image src="/images/logo.png" alt="Chii-Piyo" width={300} height={100} className="mx-auto" />
      <nav className="flex justify-center gap-12 mt-4">
        <Link href="/">ホーム</Link>
        <Link href="/media">写真・動画</Link>
        <Link href="/care">育児記録</Link>
        <Link href="/settings">設定</Link>
      </nav>
    </footer>
  );
}
