"use client";

import { useEffect, useState } from "react";

/**
 * 現在のスクロール位置がページの最下部に達しているかを判定
 * @returns 真偽値 - 最下部に達している場合はtrue、そうでない場合はfalse
 */
export const useIsBottomScroll = () => {
  const [isBottom, setIsBottom] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      // スクロール位置が最下部に達しているかを判定
      const reachedBottom =
        window.innerHeight + Math.round(window.scrollY) >=
        document.documentElement.scrollHeight - 1;
      setIsBottom(reachedBottom);
    };

    // スクロールイベントと初期判定処理を登録
    window.addEventListener("scroll", handleScroll, { passive: true });
    handleScroll();

    // クリーンアップ関数でイベントリスナーを削除
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  return isBottom;
};
