"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { AlbumResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateNonTime } from "@/utils/date";

import { deleteAlbumAction } from "../../actions/deleteAlbumAction";

type Props = {
  album: AlbumResponseDto;
  index: number;
};

export const SettingsAlbumListItem = ({ album, index }: Props) => {
  const [isDeleteConfirm, setIsDeleteConfirm] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteAlbumAction({
        albumId: album.id,
      });

      if (result.success) {
        setIsDeleteConfirm(false);
        toast.success("アルバムの削除に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      <div
        className={`flex items-center justify-between px-8 py-4 max-lg:px-4 max-md:flex-col max-md:items-start max-md:px-5 ${index > 0 ? "border-brown-dark/50 border-t" : ""}`}
      >
        <div className="flex w-full items-center justify-between max-md:flex-col max-md:items-start">
          <div className="flex items-start gap-8 max-md:gap-4">
            <Image
              src={album.coverMediaUrls[0] ?? "/images/no-image.svg"}
              alt=""
              width={80}
              height={80}
              className="aspect-square object-cover"
            />
            <div className="grid gap-1">
              <p className="max-md:text-[13px]">アルバム名 : {album.title}</p>
              <p className="text-sm max-md:text-[10px]">
                作成日 : {formatJapaneseDateNonTime(album.createdAt)}
              </p>
              <div className="flex gap-2">
                <p className="text-sm max-md:text-[10px]">写真 : {album.photoCount}</p>
                <p className="text-sm max-md:text-[10px]">動画 : {album.videoCount}</p>
              </div>
            </div>
          </div>
          <div className="flex shrink-0 gap-5 max-md:ml-auto">
            <button className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]">
              編集
            </button>
            <button
              className="text-warning cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
              onClick={() => setIsDeleteConfirm(true)}
            >
              削除
            </button>
          </div>
        </div>
      </div>
      <AnimatePresence>
        {isDeleteConfirm && (
          <Modal>
            <ActionDialog onClose={isPending ? undefined : () => setIsDeleteConfirm(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  アルバム【{album.title}】を削除します。
                  <br />
                  本当によろしいですか？
                </p>
                <div className="flex justify-center gap-5">
                  <Button
                    variant="cancel"
                    onClick={() => setIsDeleteConfirm(false)}
                    disabled={isPending}
                  >
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
