"use client";

import Image from "next/image";
import Link from "next/link";

import { cn } from "@/utils/cn";

type Props = {
  isAdmin: boolean;
  isEasy: boolean;
};

export const BottomNavigation = ({ isAdmin, isEasy }: Readonly<Props>) => {
  return (
    <nav
      className={cn(
        "bg-background dark:border-brown-light dark:bg-translucent fixed bottom-0 z-10 grid h-16 w-full grid-cols-5 shadow-[0_-4px_10px_rgba(21,12,0,0.1)] transition-all duration-400 @md:hidden dark:border-t dark:backdrop-blur-sm",
        isEasy && "max-w-125",
      )}
    >
      {isEasy ? (
        <>
          <MenuItem href="/media?mediaKind=PHOTO" icon="2" label="写真一覧" variant="easy" />
          <MenuItem href="/media?mediaKind=VIDEO" icon="6" label="動画一覧" variant="easy" />
          <MenuItem href="/albums" icon="7" label="アルバム" variant="easy" />
          <MenuItem href="/analysis" icon="3" label="記録" variant="easy" />
          <MenuItem href="/upload" icon="4" label="アップロード" variant="easy" />
        </>
      ) : (
        <>
          <MenuItem href="/" icon="1" label="ホーム" variant="normal" />
          <MenuItem href="/media" icon="2" label="写真/動画" variant="normal" />
          <MenuItem href={isAdmin ? "/care" : "/analysis"} icon="3" label="記録" variant="normal" />
          <MenuItem href="/upload" icon="4" label="アップロード" variant="normal" />
          <MenuItem href="/settings" icon="5" label="設定" variant="normal" />
        </>
      )}
    </nav>
  );
};

const MenuItem = ({
  href,
  icon,
  label,
  variant,
}: {
  href: string;
  icon: string;
  label: string;
  variant: "easy" | "normal";
}) => {
  return (
    <Link href={href} className="grid place-content-center">
      <Image
        src={`/images/nav-icon-${icon}.svg`}
        alt=""
        width={44}
        height={44}
        className="mx-auto dark:hidden"
      />
      <Image
        src={`/images/nav-icon-${icon}-white.svg`}
        alt=""
        width={44}
        height={44}
        className="mx-auto hidden dark:block"
      />
      <p
        className={cn(
          "text-brown-dark -mt-1 text-center font-medium dark:text-white",
          variant === "easy" ? "text-[11px]" : "text-[10px]",
        )}
      >
        {label}
      </p>
    </Link>
  );
};
