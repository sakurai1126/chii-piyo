"use client";
import Image from "next/image";

import { UserResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import icon from "../assets/display-mode.svg";
import whiteIcon from "../assets/white/display-mode.svg";
import { useChangeSettings } from "../hooks/useChangeSettings";

type Props = {
  isEasy: boolean;
  currentUser: UserResponseDto;
};

export const DisplayMode = ({ isEasy, currentUser }: Props) => {
  const { isDarkMode, darkModeChange, isEasyMode, easyModeChange } = useChangeSettings({
    currentUser,
  });

  return (
    <div className="mt-10 @max-md:mt-8" id="display-mode">
      <div className="flex items-center gap-2">
        <Image
          src={icon}
          alt=""
          width={30}
          height={30}
          className="max-md:w-6 @max-md:h-6 dark:hidden"
        />
        <Image
          src={whiteIcon}
          alt=""
          width={30}
          height={30}
          className="hidden @max-md:h-6 @max-md:w-6 dark:block"
        />
        <p className={cn("font-medium", isEasy ? "text-[18px]" : "@max-md:text-[13px]")}>
          表示モード
        </p>
      </div>
      <div className="bg-background-normal dark:bg-background-accent border-brown-dark mt-4 rounded-lg border @max-md:mt-3">
        <div className="flex items-center justify-between px-8 py-6 @max-lg:px-4 @max-md:px-5 @max-md:py-4">
          <p className={isEasy ? "font-medium" : "@max-md:text-[13px]"}>ダークモード</p>
          <label
            className="group relative h-7 w-16 cursor-pointer @max-md:h-6 @max-md:w-13"
            aria-label="ダークモードの切り替え"
          >
            <input
              type="checkbox"
              className="peer hidden"
              checked={isDarkMode}
              onChange={darkModeChange}
            />
            <span className="peer-checked:bg-accent-orange bg-line-gray absolute inset-0 rounded-full transition-colors"></span>
            <span className="bg-light-dark absolute top-0.75 left-0.75 h-5.5 w-5.5 scale-90 rounded-full transition-transform group-hover:scale-100 peer-checked:translate-x-8.75 @max-md:top-0.5 @max-md:h-5 @max-md:w-5 @max-md:peer-checked:translate-x-6.5"></span>
          </label>
        </div>
        <div className="border-brown-dark/50 flex items-center justify-between border-t px-8 py-6 @max-lg:px-4 @max-md:px-5 @max-md:py-4">
          <p className={isEasy ? "font-medium" : "@max-md:text-[13px]"}>かんたんモード</p>
          <label
            className="group relative h-7 w-16 cursor-pointer @max-md:h-6 @max-md:w-13"
            aria-label="かんたんモードの切り替え"
          >
            <input
              type="checkbox"
              className="peer hidden"
              checked={isEasyMode}
              onChange={easyModeChange}
            />
            <span className="peer-checked:bg-accent-orange bg-line-gray absolute inset-0 rounded-full transition-colors"></span>
            <span className="bg-light-dark absolute top-0.75 left-0.75 h-5.5 w-5.5 scale-90 rounded-full transition-transform group-hover:scale-100 peer-checked:translate-x-8.75 @max-md:top-0.5 @max-md:h-5 @max-md:w-5 @max-md:peer-checked:translate-x-6.5"></span>
          </label>
        </div>
      </div>
    </div>
  );
};
