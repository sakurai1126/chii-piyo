"use client";

import { AnimatePresence } from "motion/react";
import { useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";

import { deleteMediaAction } from "../../actions/deleteMediaAction";

type Props = {
  mediaId: number;
};
export const MediaDelete = ({ mediaId }: Props) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteMediaAction({ mediaId });

      if (result.success) {
        // 削除後はモーダル等の挙動制御等もあるためキャッシュ制御ではなくフルロードで一覧画面に繊維
        window.location.href = "/media?deleted=true";
      } else {
        toast.error(result.error);
      }
    });
  };
  return (
    <>
      <button
        className="text-warning mt-4 ml-auto block w-fit cursor-pointer text-xs underline transition-all hover:opacity-70"
        onClick={() => setIsOpen(true)}
      >
        メディアをゴミ箱に移動する
      </button>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsOpen(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  選択したメディアをゴミ箱に移動します。
                  <br />
                  本当によろしいですか？
                </p>

                <div className="flex justify-center gap-5">
                  <Button variant="cancel" onClick={() => setIsOpen(false)} disabled={isPending}>
                    キャンセル
                  </Button>
                  <Button variant="remove" onClick={deleteAction} disabled={isPending}>
                    削除する
                  </Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </>
  );
};
