"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";

export const AlbumMediaDetail = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);

  return (
    <div className="mt-7">
      <p className="max-md:text-sm">アルバム</p>
      <div className="border-brown-dark mt-2 flex justify-between rounded-lg border bg-[rgba(255,255,255,0.5)] p-4 backdrop-blur-[7.5px] max-md:flex-col">
        <div className="flex gap-3">
          <Image
            src="/images/mock-img.jpg"
            alt=""
            width={80}
            height={80}
            className="aspect-square h-20 w-20 rounded-sm object-cover"
          />
          <div>
            <p className="text-sm max-md:text-[13px]">タイトル</p>
            <p className="mt-1 text-xs max-md:text-[11px]">画像：1枚</p>
            <p className="mt-1 text-xs max-md:text-[11px]">動画：1本</p>
          </div>
        </div>
        <div className="flex flex-col items-end justify-between max-md:mt-3 max-md:flex-row max-md:items-center">
          <Button className="w-32 max-md:h-8 max-md:w-30">アルバムを見る</Button>
          <button
            className="text-warning cursor-pointer text-xs underline transition-all hover:opacity-70 max-md:text-[10px]"
            onClick={() => setIsOpen(true)}
          >
            アルバムから削除する
          </button>
        </div>
      </div>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsOpen(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  選択したメディアをアルバムから削除します。
                  <br />
                  本当によろしいですか？
                </p>

                <div className="flex justify-center gap-5">
                  <Button variant="cancel" onClick={() => setIsOpen(false)}>
                    キャンセル
                  </Button>
                  <Button variant="remove">削除する</Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
