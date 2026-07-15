"use client";

import { useEffect, useState } from "react";

import { cn } from "@/utils/cn";

// トーストの種別
type ToastType = "success" | "error";

// トーストのデータ型
type ToastItem = {
  id: string;
  type: ToastType;
  message: string;
};

// カスタムイベント名
const TOAST_EVENT = "toast:show";

// トーストを発火するための関数
export const toast = {
  success: (message: string) => dispatchToast("success", message),
  error: (message: string) => dispatchToast("error", message),
};

// 受け取ったパラメータを元にカスタムイベントを作成して即時発火する関数
const dispatchToast = (type: ToastType, message: string) => {
  dispatchEvent(
    new CustomEvent<Omit<ToastItem, "id">>(TOAST_EVENT, {
      detail: { type, message },
    }),
  );
};

// 表示時間（ms）
const DURATION = 5000;

// トーストの表示を管理するコンポーネント
export default function Toast() {
  // トーストアイテム
  // closingはフェードアウトの開始を示すフラグ
  const [items, setItems] = useState<(ToastItem & { closing: boolean })[]>([]);

  // トーストのフェードアウトを開始する関数
  const startFadeOut = (id: string) => {
    setItems((prev) => prev.map((item) => (item.id === id ? { ...item, closing: true } : item)));
  };

  // トーストを削除する関数
  const removeToast = (id: string) => {
    setItems((prev) => prev.filter((item) => item.id !== id));
  };

  // トーストの表示を監視するエフェクト
  useEffect(() => {
    const handler = (e: Event) => {
      const { type, message } = (e as CustomEvent<Omit<ToastItem, "id">>).detail;
      const id = crypto.randomUUID();

      // 新しいトーストアイテムを追加
      setItems((prev) => [...prev, { id, type, message, closing: false }]);

      // 表示時間分の遅延後後にフェードアウト開始
      setTimeout(() => startFadeOut(id), DURATION);

      // アニメーション完了後(400ms)に削除
      setTimeout(() => removeToast(id), DURATION + 400);
    };

    addEventListener(TOAST_EVENT, handler);

    return () => removeEventListener(TOAST_EVENT, handler);
  }, []);

  if (items.length === 0) return null;

  return (
    <div className="fixed top-10 right-0 left-0 z-50 mx-auto grid w-fit gap-2">
      {items.map((item) => {
        return (
          <div
            key={item.id}
            className={cn(
              "flex translate-y-0 items-center gap-3 rounded-xl px-4 py-3 opacity-100 shadow-lg transition-all duration-400 max-md:min-w-60",
              item.closing && "translate-y-2 opacity-0",
              item.type === "success" && "bg-success",
              item.type === "error" && "bg-warning-back",
            )}
          >
            {/* メッセージ */}
            <p className="text-sm text-white">{item.message}</p>
            {/* 閉じるボタン */}
            <button
              type="button"
              onClick={() => startFadeOut(item.id)}
              className="shrink-0 cursor-pointer text-xs text-white transition-all duration-500 hover:opacity-50"
            >
              ✕
            </button>
          </div>
        );
      })}
    </div>
  );
}
