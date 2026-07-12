"use client";

import { useState, useTransition } from "react";

import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
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
      <div className="border-line-gray mt-10 flex items-center justify-between border-t pt-7 @max-md:flex-col @max-md:items-start">
        <div>
          <p className="text-warning text-sm @max-md:text-xs dark:font-medium">ゴミ箱を空にする</p>
          <p className="mt-2 text-sm @max-md:text-xs">
            すべてのメディアを完全に削除します。
            <br className="@md:hidden" />
            この操作は取り消せません。
          </p>
        </div>
        <Button
          variant="remove"
          className="w-fit px-6 @max-md:mt-4 @max-md:ml-auto"
          onClick={() => setIsOpen(true)}
        >
          ゴミ箱を空にする
        </Button>
      </div>
      <ConfirmModal
        isOpen={isOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsOpen(false)}
        message="ゴミ箱内のメディアを完全に削除します。"
        buttonType="remove"
        buttonMessage="削除する"
      />
    </>
  );
};
