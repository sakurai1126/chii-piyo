"use client";

import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { TagResponseDto } from "@/lib/api-client/gen";

import { updateMediaTagsAction } from "../actions/updateMediaTagsAction";
import plus from "../assets/plus.svg";

import { TagSelector } from "./TagSelector";

type Props = {
  isAdmin: boolean;
  mediaId: number;
  mediaTags: TagResponseDto[] | undefined;
  tags: TagResponseDto[];
};

export const TagMediaDetail = ({ isAdmin, mediaId, mediaTags, tags }: Props) => {
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>(
    mediaTags?.map((tag) => tag.id) || [],
  );
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isPending, startTransition] = useTransition();

  // キャンセル処理
  const editCancel = () => {
    // 選択状態を初期値に戻す
    setSelectedTagIds(mediaTags?.map((tag) => tag.id) || []);

    // モーダルを閉じる
    setIsOpen(false);
  };

  // 保存処理
  const saveEdit = () => {
    startTransition(async () => {
      // サーバーアクションでタグを更新
      const response = await updateMediaTagsAction({
        mediaId: mediaId,
        tagIds: selectedTagIds,
      });

      if (response.success) {
        setIsOpen(false);
        toast.success("タグを編集しました");
      } else {
        toast.error("タグの編集に失敗しました");
      }
    });
  };

  return (
    <div className="mt-7 @max-md:mt-4">
      <p className="@max-md:text-sm">タグ</p>

      <div className="mt-3 flex flex-wrap gap-3">
        {mediaTags?.map((tag) => (
          <p
            key={tag.id}
            className="bg-accent-orange-back border-brown-middle text-brown-middle grid place-content-center rounded-2xl border px-4 py-1 text-sm @max-md:px-3 @max-md:text-xs"
          >
            {tag.name}
          </p>
        ))}
        {isAdmin && (
          <button
            type="button"
            className="border-line-gray text-note-gray hover:bg-line-gray flex cursor-pointer items-center gap-1 rounded-2xl border border-dashed bg-white px-3 py-1 text-sm transition-all hover:text-white @max-md:text-xs"
            onClick={() => setIsOpen(true)}
            disabled={isPending}
          >
            <Image src={plus} alt="" width={14} height={14} className="@max-md:h-3 @max-md:w-3" />
            <p>編集</p>
          </button>
        )}
      </div>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={editCancel}>
              <div className="flex h-full flex-col justify-between">
                <div className="-mt-8">
                  <TagSelector
                    tags={tags}
                    selectedTagIds={selectedTagIds}
                    onTagSelect={(tagIds) => setSelectedTagIds(tagIds)}
                  />
                </div>
                <div className="flex justify-center gap-5 @max-md:mt-8">
                  <Button variant="cancel" onClick={editCancel} disabled={isPending}>
                    キャンセル
                  </Button>
                  <Button onClick={saveEdit} disabled={isPending}>
                    保存する
                  </Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
