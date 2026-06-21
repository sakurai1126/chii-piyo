"use client";

import { useState, useTransition } from "react";

import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { TrashItemListResponseDto } from "@/lib/api-client/gen";

import { deleteTrashItemsAction } from "../actions/deleteTrashItemActions";
import { restoreTrashItemsAction } from "../actions/restoreTrashItemsAction";

import { TrashItem } from "./TrashItem";

type Props = {
  trashItems: TrashItemListResponseDto;
};

export const TrashContent = ({ trashItems }: Props) => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [isRestoreOpen, setIsRestoreOpen] = useState<boolean>(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 全選択の制御
  const allIds = trashItems.items.map((trashItem) => trashItem.id);
  const allChecked = allIds.length > 0 && selectedIds.length === allIds.length;
  const allCheck = () => setSelectedIds(allIds);
  const allClear = () => setSelectedIds([]);

  const restoreConfirmOpen = () => {
    if (selectedIds.length === 0) {
      toast.error("選択されていません。");
      return;
    }
    setIsRestoreOpen(true);
  };

  const deleteConfirmOpen = () => {
    if (selectedIds.length === 0) {
      toast.error("選択されていません。");
      return;
    }
    setIsDeleteOpen(true);
  };

  const restoreAction = () => {
    startTransition(async () => {
      const result = await restoreTrashItemsAction({ trashItemIds: selectedIds });

      if (result.success) {
        setIsRestoreOpen(false);
        setSelectedIds([]);
        toast.success("メディアを復元しました。");
      } else {
        toast.error(result.error);
      }
    });
  };

  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteTrashItemsAction({ trashItemIds: selectedIds });

      if (result.success) {
        setIsDeleteOpen(false);
        setSelectedIds([]);
        toast.success("メディアを完全に削除しました。");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      <div className="mt-10 flex gap-10 max-md:mt-8 max-md:flex-col-reverse max-md:gap-6">
        <label htmlFor="allCheck" className="flex cursor-pointer items-center gap-3">
          <input
            type="checkbox"
            id="allCheck"
            className="accent-accent-pink h-4.5 w-4.5 max-md:h-4 max-md:w-4"
            checked={allChecked}
            onChange={(e) => (e.target.checked ? allCheck() : allClear())}
          />
          <p className="text-lg max-md:text-[13px]">すべて選択</p>
        </label>
        <div className="flex gap-3 max-md:flex-col">
          <Button variant="cancel" className="w-fit px-4" onClick={restoreConfirmOpen}>
            選択したメディアを復元
          </Button>
          <Button variant="remove" className="w-fit px-4" onClick={deleteConfirmOpen}>
            選択したメディアを完全に削除
          </Button>
        </div>
      </div>
      <div className="mt-10 grid gap-5 max-md:mt-8">
        {trashItems.items.map((trashItem) => (
          <TrashItem
            key={trashItem.id}
            trashItem={trashItem}
            selectedIds={selectedIds}
            setSelectedIds={setSelectedIds}
            // ページ全体のボタンを一時無効化するため
            isPending={isPending}
            startTransition={startTransition}
          />
        ))}
      </div>
      <ConfirmModal
        isOpen={isRestoreOpen}
        isPending={isPending}
        action={restoreAction}
        closeAction={() => setIsRestoreOpen(false)}
        message="選択したメディアを復元します。"
        buttonMessage="復元する"
      />
      <ConfirmModal
        isOpen={isDeleteOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteOpen(false)}
        message="選択したメディアを完全に削除します。"
        buttonType="remove"
        buttonMessage="完全に削除する"
      />
    </>
  );
};
