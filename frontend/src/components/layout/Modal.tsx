"use client";
import { motion } from "motion/react";
import { useRouter } from "next/navigation";
import { createContext, useCallback, useContext, useEffect, useState } from "react";

// 子コンポーネントから「閉じる」を呼ぶためのContext
const ModalCloseContext = createContext<() => void>(() => {});
export const useModalClose = () => useContext(ModalCloseContext);

type Props = {
  children: React.ReactNode;
  className?: string;
};

export const Modal = ({ children, className }: Readonly<Props>) => {
  const [isReturning, setIsReturning] = useState(false);
  const router = useRouter();

  useEffect(() => {
    // 元のoverflowの値を保存
    const originalStyle = getComputedStyle(document.body).overflow;
    // 背景のスクロールを止める
    document.body.style.overflow = "hidden";
    return () => {
      // モーダルが閉じられたら元の値に戻す
      document.body.style.overflow = originalStyle;
    };
  }, []);

  // motionだけでは画面遷移時にフェードアニメーションが効かないので、フェードしてからsetTimeoutで画面遷移する
  useEffect(() => {
    if (!isReturning) return;
    setTimeout(() => router.back(), 300);
  }, [isReturning, router]);

  const handleClose = useCallback(() => setIsReturning(true), []);

  return (
    <ModalCloseContext.Provider value={handleClose}>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        transition={{ duration: 0.3 }}
      >
        <div
          className={`${isReturning ? "animate-fade-out" : "animate-fade-in"} fixed top-0 left-0 z-100 h-full w-full overflow-y-auto bg-[rgba(255,255,231,0.7)] backdrop-blur-[7.5px] ${className}`}
        >
          {children}
        </div>
      </motion.div>
    </ModalCloseContext.Provider>
  );
};
