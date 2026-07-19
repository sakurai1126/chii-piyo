"use client";

import { motion } from "motion/react";
import { RemoveScroll } from "react-remove-scroll";

import { cn } from "@/utils/cn";

type Props = {
  children: React.ReactNode;
  className?: string;
};

export const Modal = ({ children, className }: Readonly<Props>) => {
  return (
    <RemoveScroll>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        transition={{ duration: 0.3 }}
      >
        {/* ぼかし背景用レイヤー - ※追加でモーダルを表示するとbackdrop-blurにより起点がずれてしまうため分離 */}
        <div className="bg-modal-back fixed top-0 left-0 z-100 h-full w-full backdrop-blur-[7.5px]" />

        <div className={cn("fixed top-0 left-0 z-100 h-full w-full overflow-y-auto", className)}>
          {children}
        </div>
      </motion.div>
    </RemoveScroll>
  );
};
