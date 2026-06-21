"use client";

import { useState, useTransition } from "react";

import { ConfirmModal } from "@/components/ui/ConfirmModal";
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
        globalThis.location.href = "/media?deleted=true";
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
      <ConfirmModal
        isOpen={isOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsOpen(false)}
        message="選択したメディアをゴミ箱に移動します。"
        buttonType="remove"
        buttonMessage="実行する"
      />
    </>
  );
};
