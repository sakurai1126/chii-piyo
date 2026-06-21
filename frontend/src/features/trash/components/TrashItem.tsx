"use client";

import Image from "next/image";
import { Dispatch, SetStateAction, TransitionStartFunction, useId, useState } from "react";

import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { TrashItemResponseDto } from "@/lib/api-client/gen";
import { calculateRemainingDays, formatJapaneseDateNonTime } from "@/utils/date";

import { deleteTrashItemAction } from "../actions/deleteTrashItemAction";
import { restoreTrashItemAction } from "../actions/restoreTrashItemAction";

type Props = {
  trashItem: TrashItemResponseDto;
  selectedIds: number[];
  setSelectedIds: Dispatch<SetStateAction<number[]>>;
  isPending: boolean;
  startTransition: TransitionStartFunction;
};
export const TrashItem = ({
  trashItem,
  selectedIds,
  setSelectedIds,
  isPending,
  startTransition,
}: Props) => {
  const uid = useId();
  const sizeInKB = (trashItem.media.fileSize / 1024).toFixed(0);
  const sizeInMB = (trashItem.media.fileSize / 1024 / 1024).toFixed(1);
  const [isRestoreOpen, setIsRestoreOpen] = useState<boolean>(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState<boolean>(false);

  // 選択する処理
  const addSelectedId = (id: number) => setSelectedIds((prev) => [...prev, id]);

  // 選択を解除する処理
  const removeSelectedId = (id: number) =>
    setSelectedIds((prev) => prev.filter((prevId) => prevId !== id));

  // 復元処理
  const restoreAction = () => {
    startTransition(async () => {
      const result = await restoreTrashItemAction({ trashItemId: trashItem.id });
      if (result.success) {
        successAfterReset();
        toast.success("メディアを復元しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  // 削除処理
  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteTrashItemAction({ trashItemId: trashItem.id });
      if (result.success) {
        successAfterReset();
        toast.success("メディアを完全に削除しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const successAfterReset = () => {
    // モーダルを閉じる
    setIsRestoreOpen(false);
    setIsDeleteOpen(false);
    // 成功時、選択中IDリストから自身のIDを除外
    setSelectedIds((prev) => prev.filter((id) => id !== trashItem.id));
  };

  return (
    <div>
      <div className="bg-white-back border-brown-dark flex items-center justify-between rounded-lg border py-5 pr-12 pl-7 max-md:flex-col max-md:items-start max-md:px-5 max-md:py-4">
        <div className="flex items-center gap-7 max-md:gap-4">
          <label
            htmlFor={`trashItem-${uid}`}
            className="flex cursor-pointer items-center gap-5 max-md:gap-3"
          >
            <input
              type="checkbox"
              id={`trashItem-${uid}`}
              className="accent-accent-pink h-4.5 w-4.5 max-md:h-4 max-md:w-4"
              checked={selectedIds.includes(trashItem.id)}
              onChange={(e) =>
                e.target.checked ? addSelectedId(trashItem.id) : removeSelectedId(trashItem.id)
              }
              disabled={isPending}
            />
            <Image
              src={trashItem.media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
              alt=""
              className="aspect-square rounded-lg object-cover max-md:h-20 max-md:w-20"
              width={140}
              height={140}
            />
          </label>
          <div>
            <p className="max-md:text-xs">{trashItem.media.originalFilename}</p>
            <p className="mt-1 text-[13px] max-md:text-[11px]">
              {Number(sizeInKB) >= 1024 ? `${sizeInMB}MB` : `${sizeInKB}KB`} {trashItem.media.width}{" "}
              × {trashItem.media.height}
            </p>
            <p className="mt-1 text-[13px] max-md:text-[11px]">
              削除日：{formatJapaneseDateNonTime(trashItem.expiresAt)}
            </p>
            <p className="text-warning mt-1 text-[13px] max-md:text-[11px]">
              あと{calculateRemainingDays(trashItem.expiresAt)}日
            </p>
          </div>
        </div>
        <div className="flex gap-5 max-lg:flex-col max-md:mt-5 max-md:flex-row max-md:gap-4">
          <Button variant="cancel" disabled={isPending} onClick={() => setIsRestoreOpen(true)}>
            復元する
          </Button>
          <Button variant="remove" disabled={isPending} onClick={() => setIsDeleteOpen(true)}>
            完全に削除する
          </Button>
        </div>
      </div>
      <ConfirmModal
        isOpen={isRestoreOpen}
        isPending={isPending}
        action={restoreAction}
        closeAction={() => setIsRestoreOpen(false)}
        message={`${trashItem.media.originalFilename} を復元します。`}
        buttonMessage="復元する"
      />
      <ConfirmModal
        isOpen={isDeleteOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteOpen(false)}
        message={`${trashItem.media.originalFilename} を削除します。`}
        buttonType="remove"
        buttonMessage="削除する"
      />
    </div>
  );
};
