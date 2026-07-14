"use client";

import { logoutAction } from "@/features/auth/actions/logout";
import { UserResponseDto } from "@/lib/api-client/gen";

import { useChangeSettings } from "../hooks/useChangeSettings";

type Props = {
  currentUser: UserResponseDto;
};

export const DisplayDebug = ({ currentUser }: Props) => {
  const { isDarkMode, darkModeChange, isEasyMode, easyModeChange } = useChangeSettings({
    currentUser,
  });

  return (
    <div className="bg-background border-brown-dark fixed right-3 bottom-5 z-1000 w-50 rounded-lg border px-2 py-3">
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
          onClick={logoutAction}
          className="text-black-text border-line-gray rounded-sm border bg-white px-3 py-2 text-xs"
        >
          ログアウト
        </button>
      </div>
    </div>
  );
};
