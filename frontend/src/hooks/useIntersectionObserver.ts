"use client";

import { useEffect, useRef } from "react";

type Props = {
  callback: () => void;
  enabled: boolean;
};

/**
 * 特定の要素がビューポートに入ったときにコールバックするカスタムフック
 * refを返すので、監視対象の要素にrefを設定することでその要素がビューポートに入ったときに指定したコールバック関数が呼び出される
 *
 * @param callback
 * 呼び出されるコールバック関数
 *
 * @param enabled
 * 有効化制御フラグ(すべて表示しきっている場合など一時的に無効化したいときに使用)
 *
 * @returns
 * 監視対象の要素を参照するためのref ※div要素を想定
 */
export const useIntersectionObserver = ({ callback, enabled }: Props) => {
  // 監視対象の要素を参照するためのref
  const ref = useRef<HTMLDivElement>(null);

  // コールバック関数を最新のものに保つためのref
  const callbackRef = useRef(callback);
  // レンダリング毎にコールバック関数を更新
  useEffect(() => {
    callbackRef.current = callback;
  });

  useEffect(() => {
    // 無効化設定の場合やrefがまだ要素を参照していない場合
    if (!enabled || !ref.current) return;

    // IntersectionObserverの設定を構築しインスタンス化
    const observer = new IntersectionObserver(
      (entries) => {
        // 最初のエントリーが表示されている場合にcallbackを呼び出す
        if (entries[0].isIntersecting) callbackRef.current();
      },
      // 発火閾値: 要素が10%表示されたとき
      { threshold: 0.1 },
    );

    // refを監視対象として登録
    observer.observe(ref.current);

    // クリーンアップ
    return () => observer.disconnect();
  }, [enabled]);

  return ref;
};
