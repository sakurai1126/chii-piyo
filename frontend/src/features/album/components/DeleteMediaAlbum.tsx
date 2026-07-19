"use client";

import { useQueryClient } from "@tanstack/react-query";
import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";

import { deleteAlbumMediaAction } from "../actions/deleteAlbumMediaAction";

type Props = {
  albumId: number;
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  selectedMedia: number[];
  setSelectedMedia: Dispatch<SetStateAction<number[]>>;
};

export const DeleteMediaAlbum = ({
  albumId,
  isOpen,
  setIsOpen,
  selectedMedia,
  setSelectedMedia,
}: Props) => {
  const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();
  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  const confirmOpen = () => {
    if (!selectedMedia.length) {
      toast.error("メディアを選択してください");
      return;
    }

    if (selectedMedia.length > 100) {
      toast.error("一度に削除可能なメディアの数は100件までです");
      return;
    }

    setIsConfirmOpen(true);
  };

  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteAlbumMediaAction({
        albumId,
        mediaIds: selectedMedia,
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["media"] });
        setSelectedMedia([]);
        setIsConfirmOpen(false);
        setIsOpen(false);

        toast.success("選択したメディアをアルバムから削除しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      {isOpen && (
        <Button
          className="mt-3 ml-auto flex w-fit items-center justify-center gap-2 px-7 @max-md:mt-3 @max-md:px-3"
          variant="remove"
          onClick={confirmOpen}
        >
          <p className="@max-md:text-xs">選択したメディアをアルバムから削除</p>
        </Button>
      )}

      <ConfirmModal
        isOpen={isConfirmOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsConfirmOpen(false)}
        message="選択したメディアをアルバムから削除します。"
        buttonType="remove"
        buttonMessage="実行する"
      />
    </>
  );
};
