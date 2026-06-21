"use client";

import Image from "next/image";
import { useState, useTransition } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { AlbumResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateNonTime } from "@/utils/date";

import { deleteAlbumAction } from "../../actions/deleteAlbumAction";
import { updateAlbumAction } from "../../actions/updateAlbumAction";

type Props = {
  album: AlbumResponseDto;
  index: number;
};

export const SettingsAlbumListItem = ({ album, index }: Props) => {
  const [isDeleteConfirm, setIsDeleteConfirm] = useState<boolean>(false);
  const [isEditAreaOpen, setIsEditAreaOpen] = useState<boolean>(false);
  const [newTitle, setNewTitle] = useState<string>(album.title);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 編集キャンセル
  const cancelEdit = () => {
    setNewTitle(album.title);
    setIsEditAreaOpen(false);
  };

  // 編集処理
  const saveUpdateAction = () => {
    if (!newTitle.trim()) {
      toast.error("アルバム名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await updateAlbumAction({
        albumId: album.id,
        newTitle: newTitle.trim(),
      });

      if (result.success) {
        setIsEditAreaOpen(false);
        toast.success("アルバム名の更新に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  // 削除処理
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
        className={`px-8 py-4 max-lg:px-4 max-md:px-5 ${index > 0 ? "border-brown-dark/50 border-t" : ""}`}
      >
        <div className="flex items-center justify-between max-md:flex-col max-md:items-start">
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
              {!isEditAreaOpen && (
                <button
                  className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                  onClick={() => setIsEditAreaOpen(true)}
                  disabled={isPending}
                >
                  編集
                </button>
              )}

              <button
                className="text-warning cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                onClick={() => setIsDeleteConfirm(true)}
                disabled={isPending}
              >
                削除
              </button>
            </div>
          </div>
        </div>
        <AccordionContent isOpen={isEditAreaOpen}>
          <p className="pt-5">アルバム名の編集</p>
          <input
            className="border-line-gray focus:outline-brown-light mt-2 block h-10 w-full max-w-90 rounded-sm border bg-white px-2.5"
            onChange={(e) => setNewTitle(e.target.value)}
            value={newTitle}
            disabled={isPending}
          />
          <div className="mt-4 flex gap-4">
            <Button variant="cancel" onClick={cancelEdit} disabled={isPending}>
              キャンセル
            </Button>
            <Button onClick={saveUpdateAction} disabled={isPending}>
              保存
            </Button>
          </div>
        </AccordionContent>
      </div>

      <ConfirmModal
        isOpen={isDeleteConfirm}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteConfirm(false)}
        message={`アルバム【${album.title}】を削除します。`}
        buttonType="remove"
        buttonMessage="削除する"
      />
    </>
  );
};
