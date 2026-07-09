"use client";
import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { useState, useTransition } from "react";

import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { AlbumResponseDto, MediaResponseDto } from "@/lib/api-client/gen";

import { deleteAlbumMediaAction } from "../actions/deleteAlbumMediaAction";

type Props = {
  isAdmin: boolean;
  album: AlbumResponseDto;
  media: MediaResponseDto;
};
export const AlbumMediaDetail = ({ isAdmin, album, media }: Props) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();
  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteAlbumMediaAction({
        albumId: album.id,
        mediaIds: [media.id],
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["media"] });
        setIsOpen(false);
        toast.success("メディアをアルバムから削除しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="mt-7">
      <p className="max-md:text-sm">アルバム</p>
      <div className="border-brown-dark bg-translucent mt-2 flex justify-between rounded-lg border p-4 backdrop-blur-[7.5px] max-md:flex-col">
        <div className="flex gap-3">
          <Image
            src={album.coverMediaUrls[0] ?? "/images/no-image.svg"}
            alt=""
            width={80}
            height={80}
            className="aspect-square h-20 w-20 rounded-sm object-cover"
          />
          <div>
            <p className="text-sm max-md:text-[13px]">{album.title}</p>
            <p className="mt-1 text-xs max-md:text-[11px]">画像：{album.photoCount}枚</p>
            <p className="mt-1 text-xs max-md:text-[11px]">動画：{album.videoCount}本</p>
          </div>
        </div>
        <div className="flex flex-col items-end justify-between max-md:mt-3 max-md:flex-row max-md:items-center">
          <a href={`/albums/${album.id}`}>
            <Button className="w-32 max-md:h-8 max-md:w-30">アルバムを見る</Button>
          </a>
          {isAdmin && (
            <button
              className="text-warning cursor-pointer text-xs underline transition-all hover:opacity-70 max-md:text-[10px] dark:font-medium"
              onClick={() => setIsOpen(true)}
            >
              アルバムから削除する
            </button>
          )}
        </div>
      </div>
      <ConfirmModal
        isOpen={isOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsOpen(false)}
        message="選択したメディアをアルバムから削除します。"
        buttonType="remove"
        buttonMessage="削除する"
      />
    </div>
  );
};
