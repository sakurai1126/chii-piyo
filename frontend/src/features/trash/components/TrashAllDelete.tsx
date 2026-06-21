"use client";

import { AnimatePresence } from "motion/react";
import { useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";

import { deleteAllTrashItemAction } from "../actions/deleteAllTrashItemAction";

export const TrashAllDelete = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();
  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteAllTrashItemAction();

      if (result.success) {
        setIsOpen(false);
        toast.success("ゴミ箱内のメディアを完全に削除しました");
      } else {
        toast.error(result.error);
      }
    });
  };
  return (
    <>
      <div className="border-line-gray mt-10 flex items-center justify-between border-t pt-7 max-md:flex-col max-md:items-start">
        <div>
          <p className="text-warning text-sm max-md:text-xs">ゴミ箱を空にする</p>
          <p className="mt-2 text-sm max-md:text-xs">
            すべてのメディアを完全に削除します。
            <br className="md:hidden" />
            この操作は取り消せません。
          </p>
        </div>
        <Button
          variant="remove"
          className="w-fit px-6 max-md:mt-4 max-md:ml-auto"
          onClick={() => setIsOpen(true)}
        >
          ゴミ箱を空にする
        </Button>
      </div>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={isPending ? undefined : () => setIsOpen(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  ゴミ箱内のメディアを完全に削除します。
                  <br />
                  本当によろしいですか？
                </p>
                <div className="flex justify-center gap-5">
                  <Button variant="cancel" onClick={() => setIsOpen(false)} disabled={isPending}>
                    キャンセル
                  </Button>
                  <Button disabled={isPending} onClick={deleteAction} variant="remove">
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
