"use client";

import Image from "next/image";
import { useState } from "react";

import { logoutAction } from "@/features/auth/actions";
import { UserResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import icon from "../assets/sidebar/account.svg";
import whiteIcon from "../assets/white/account.svg";
import { useChangeSettings } from "../hooks/useChangeSettings";

type Props = {
  currentUser: UserResponseDto;
};

export const DisplayDebug = ({ currentUser }: Props) => {
  const { isDarkMode, darkModeChange, isEasyMode, easyModeChange, isPending } = useChangeSettings({
    currentUser,
  });

  const [isOpen, setIsOpen] = useState<boolean>(false);
  return (
    <div
      className={cn(
        "bg-background border-brown-dark fixed right-5 bottom-5 z-1000 rounded-lg border p-2 @max-md:bottom-20",
        !isOpen && "rounded-full",
      )}
    >
      {isOpen ? (
        <div className="w-50 px-5 py-3">
          <p className="text-sm font-medium">表示モード</p>
          {/* ダークモード */}
          <div className="mt-2 flex items-center justify-between">
            <p className="text-xs">ダークモード</p>
            <label
              className="group relative h-7 w-16 cursor-pointer @max-md:h-6 @max-md:w-13"
              aria-label="ダークモードの切り替え"
            >
              <input
                type="checkbox"
                className="peer hidden"
                checked={isDarkMode}
                onChange={darkModeChange}
                disabled={isPending}
              />
              <span className="peer-checked:bg-accent-orange bg-line-gray absolute inset-0 rounded-full transition-colors"></span>
              <span className="bg-light-dark absolute top-0.75 left-0.75 h-5.5 w-5.5 scale-90 rounded-full transition-transform group-hover:scale-100 peer-checked:translate-x-8.75 @max-md:top-0.5 @max-md:h-5 @max-md:w-5 @max-md:peer-checked:translate-x-6.5"></span>
            </label>
          </div>
          {/* かんたんモード */}
          <div className="mt-2 flex items-center justify-between">
            <p className="text-xs">かんたんモード</p>
            <label
              className="group relative h-7 w-16 cursor-pointer @max-md:h-6 @max-md:w-13"
              aria-label="かんたんモードの切り替え"
            >
              <input
                type="checkbox"
                className="peer hidden"
                checked={isEasyMode}
                onChange={easyModeChange}
                disabled={isPending}
              />
              <span className="peer-checked:bg-accent-orange bg-line-gray absolute inset-0 rounded-full transition-colors"></span>
              <span className="bg-light-dark absolute top-0.75 left-0.75 h-5.5 w-5.5 scale-90 rounded-full transition-transform group-hover:scale-100 peer-checked:translate-x-8.75 @max-md:top-0.5 @max-md:h-5 @max-md:w-5 @max-md:peer-checked:translate-x-6.5"></span>
            </label>
          </div>
          {/* アカウント */}
          <p className="mt-4 text-sm font-medium">アカウント</p>
          <div className="mt-2 flex items-center justify-between">
            <p className="text-xs">ログアウト</p>
            <button
              type="button"
              onClick={logoutAction}
              className="text-black-text border-line-gray rounded-sm border bg-white px-3 py-2 text-xs"
              disabled={isPending}
            >
              ログアウト
            </button>
          </div>
          <button
            type="button"
            onClick={() => setIsOpen(false)}
            className="mx-auto mt-3 block w-fit text-sm"
            disabled={isPending}
          >
            close
          </button>
        </div>
      ) : (
        <button
          type="button"
          onClick={() => setIsOpen(true)}
          className="mx-auto block w-fit text-sm"
          disabled={isPending}
        >
          <Image src={icon} alt="" width={20} height={20} className="dark:hidden" />
          <Image src={whiteIcon} alt="" width={20} height={20} className="hidden dark:block" />
        </button>
      )}
    </div>
  );
};
