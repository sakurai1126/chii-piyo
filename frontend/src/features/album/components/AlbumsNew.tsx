"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { AccentButton } from "@/components/ui/AccentButton";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";

import { createAlbumAction } from "../actions/createAlbumAction";
import media from "../assets/media-white.svg";

export const AlbumsNew = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isPending, startTransition] = useTransition();
  const [newTitle, setNewTitle] = useState<string>("");

  const cancel = () => {
    setNewTitle("");
    setIsOpen(false);
  };

  const createAction = () => {
    if (!newTitle.trim()) {
      toast.error("アルバム名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await createAlbumAction({ title: newTitle.trim() });

      if (result.success) {
        setNewTitle("");
        setIsOpen(false);
        toast.success("アルバムを作成しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      <div className="relative z-100">
        <AnimatePresence>
          {isOpen && (
            <Modal>
              <ActionDialog onClose={cancel}>
                <div>
                  <p className="max-md:mt-4 max-md:text-[13px]">アルバムを新規作成</p>
                  <div className="mx-auto mt-7 w-full max-w-110">
                    <p className="text-sm">アルバム名</p>
                    <input
                      type="text"
                      className="border-line-gray focus:outline-brown-light bg-light-dark mt-3 w-full rounded-sm border p-2 text-sm"
                      value={newTitle}
                      onChange={(e) => setNewTitle(e.target.value)}
                      disabled={isPending}
                    />
                  </div>
                  <div className="mt-10 flex justify-center gap-5 max-md:mt-8">
                    <Button variant="cancel" onClick={cancel} disabled={isPending}>
                      キャンセル
                    </Button>
                    <Button onClick={createAction} disabled={isPending}>
                      保存する
                    </Button>
                  </div>
                </div>
              </ActionDialog>
            </Modal>
          )}
        </AnimatePresence>
      </div>

      <AccentButton variant="button" onClick={() => setIsOpen(true)}>
        <p>アルバムを新規作成</p>
        <Image src={media} width={16} height={16} alt="" />
      </AccentButton>
    </>
  );
};
