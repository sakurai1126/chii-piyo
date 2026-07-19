import Image from "next/image";
import Link from "next/link";

import { isAdminUser, isEasyMode } from "@/features/auth/server";
import { cn } from "@/utils/cn";

export const Footer = async () => {
  const [isAdmin, isEasy] = await Promise.all([isAdminUser(), isEasyMode()]);

  return (
    <footer
      className={cn(
        "border-brown-dark mt-40 border-t pt-10 pb-15 @max-md:mt-20 @max-md:pt-6 @max-md:pb-20",
        isEasy && "pb-10",
      )}
    >
      <Image
        src="/images/logo.png"
        alt="Chii-Piyo"
        width={300}
        height={100}
        className="mx-auto @max-md:w-45"
      />
      {!isEasy && (
        <nav className="mt-4 flex justify-center gap-12 @max-md:hidden">
          <Link href="/">ホーム</Link>
          <Link href="/media">写真・動画</Link>
          <Link href="/albums">アルバム</Link>
          <Link href="/favorites">お気に入り</Link>
          <Link href={isAdmin ? "/care" : "/analysis"}>育児記録</Link>
          <Link href="/settings">設定</Link>
        </nav>
      )}
    </footer>
  );
};
