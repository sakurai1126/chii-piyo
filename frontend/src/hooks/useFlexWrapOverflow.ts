"use client";

import { useEffect, useRef, useState } from "react";

export const useFlexWrapOverflow = (depsLength: number) => {
  // 折り返し要素があるかどうかの判別
  const [hasOverflow, setHasOverflow] = useState<boolean>(false);

  // 折り返しの有無を判定中かどうか
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // アニメーション用に高さを保持
  const [fullHeight, setFullHeight] = useState<number>(0);

  // 一行分の高さ
  const [closedHeight, setClosedHeight] = useState<number>(34);

  // 折り返しを判定する要素のref
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // 要素が存在しない場合は折り返しなしとする
    const container = ref.current;
    if (!container) return;

    // 子要素のoffsetTopを比較して折り返しの有無を判定
    const checkOverflow = () => {
      const items = container.children;
      if (items.length === 0) {
        setHasOverflow(false);
        setIsLoading(false);
        return;
      }

      setClosedHeight(window.innerWidth <= 767 ? 28 : 34);

      // 最初の要素のoffsetTopを基準に、他の要素のoffsetTopと比較する
      const firstItem = items[0] as HTMLElement;
      const firstTop = firstItem.offsetTop;
      // 合わせて一行分の高さをセット
      setClosedHeight(firstItem.offsetHeight);
      let overflow = false;

      for (let i = 1; i < items.length; i++) {
        if ((items[i] as HTMLElement).offsetTop !== firstTop) {
          overflow = true;
          break;
        }
      }

      // 折り返しの有無と高さを状態にセットし判定完了
      setHasOverflow(overflow);
      setFullHeight(container.scrollHeight);
      setIsLoading(false);
    };

    // 初回レンダリング時に折り返しの有無を判定
    checkOverflow();

    // ウィンドウのリサイズ時にも折り返しの有無を再判定
    const resizeObserver = new ResizeObserver(checkOverflow);
    resizeObserver.observe(container);

    // クリーンアップ
    return () => resizeObserver.disconnect();
  }, [depsLength]);

  return { ref, hasOverflow, isLoading, closedHeight, fullHeight };
};
