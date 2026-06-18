"use client";
import { useQueryClient } from "@tanstack/react-query";
import { AnimatePresence } from "motion/react";
import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
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
          className="mt-3 ml-auto flex w-fit items-center justify-center gap-2 px-7 max-md:mt-3 max-md:px-3"
          variant="remove"
          onClick={confirmOpen}
        >
          <p className="max-md:text-xs">選択したメディアをアルバムから削除</p>
        </Button>
      )}

      <div className="relative z-100">
        <AnimatePresence>
          {isConfirmOpen && (
            <Modal>
              <ActionDialog onClose={() => setIsConfirmOpen(false)}>
                <div className="flex h-full flex-col justify-center">
                  <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                  <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                    選択したメディアをアルバムから削除します。
                    <br />
                    実行してもよろしいですか？
                  </p>
                  <div className="flex justify-center gap-5">
                    <Button
                      variant="cancel"
                      onClick={() => setIsConfirmOpen(false)}
                      disabled={isPending}
                    >
                      キャンセル
                    </Button>
                    <Button disabled={isPending} onClick={deleteAction} variant="remove">
                      実行する
                    </Button>
                  </div>
                </div>
              </ActionDialog>
            </Modal>
          )}
        </AnimatePresence>
      </div>
    </>
  );
};
