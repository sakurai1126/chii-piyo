"use client";

import Image from "next/image";
import { usePathname, useSearchParams } from "next/navigation";
import { useState } from "react";

import { cn } from "@/utils/cn";

type Props = {
  isEasy: boolean;
  children: React.ReactNode;
};

export const HeaderBtn = ({ isEasy, children }: Props) => {
  const [isOpen, setIsOpen] = useState(false);

  // 現在のURL(パス+パラメータ)
  const pathname = usePathname();
  const searchParams = useSearchParams();

  // 画面遷移時及びブラウザバック時に開いたままにならないように
  // URLの変更を検知した際に直前のURLを更新しつつメニューを閉じる
  const currentUrl = `${pathname}?${searchParams.toString()}`;
  const [prevUrl, setPrevUrl] = useState(currentUrl);

  if (currentUrl !== prevUrl) {
    setPrevUrl(currentUrl);
    setIsOpen(false);
  }

  return (
    <div
      className={cn(
        "fixed top-11 z-100 @max-md:top-6 @max-md:right-auto @max-md:h-7 @max-md:w-7",
        isEasy ? "left-[calc(50%-230px)] max-[500px]:left-5" : "right-10 max-md:left-5",
      )}
    >
      <div>
        {/* ハンバーガーボタン */}
        {/* ボタンがクリックされたとき、現在のパスが開いているパスと同じならnullにして閉じ、相違している場合そのパスをセットする */}
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="bg-brown-light border-brown-dark flex h-10 w-10 cursor-pointer flex-col items-center justify-center gap-1.5 rounded-sm border @max-md:h-7 @max-md:w-7 @max-md:gap-1"
        >
          <div
            className={cn(
              "h-px w-5 rounded-xs bg-white transition @max-md:w-4",
              isOpen && "translate-y-1.75 rotate-45 @max-md:translate-y-1.25",
            )}
          ></div>
          <div
            className={cn(
              "h-px w-5 rounded-xs bg-white transition @max-md:w-4",
              isOpen && "opacity-0",
            )}
          ></div>
          <div
            className={cn(
              "h-px w-5 rounded-xs bg-white transition @max-md:w-4",
              isOpen && "-translate-y-1.75 -rotate-45 @max-md:-translate-y-1.25",
            )}
          ></div>
        </button>
        {/* ボタン上部のひよこ */}
        <Image
          src="/images/open-piyo.png"
          alt="ひよこ"
          width={34}
          height={37}
          className={cn(
            "absolute -top-8.25 -right-1.5 opacity-0 @max-md:top-[-21px] @max-md:right-[3px] @max-md:w-5.5 @max-md:-scale-x-100",
            isOpen && "opacity-100",
          )}
        />
      </div>
      <div
        className={cn("opacity-0 transition-all", isOpen ? "opacity-100" : "pointer-events-none")}
      >
        {children}
      </div>
    </div>
  );
};
