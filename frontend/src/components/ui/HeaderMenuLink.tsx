"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { cn } from "@/utils/cn";

type Props = {
  href: string;
  text: string;
  variant?: "page" | "parent";
};
export const HeaderMenuLink = ({ href, text, variant = "page" }: Props) => {
  // 現在のURLパス
  const pathname = usePathname();

  // 現在のパスが対象と前方一致しているかを確認
  const isCurrentpage =
    href === "/" ? pathname === "/" : pathname === href || pathname.startsWith(`${href}/`);

  const styles = {
    page: "block w-fit text-sm @max-md:text-xs",
    parent: "block w-fit font-medium @max-md:text-[13px]",
  };

  return (
    <>
      {isCurrentpage ? (
        <p className={cn(styles[variant], "opacity-40")}>{text}</p>
      ) : (
        <Link href={href} className={cn(styles[variant], "transition-all hover:opacity-70")}>
          {text}
        </Link>
      )}
    </>
  );
};
