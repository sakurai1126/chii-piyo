"use client";

import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { SharingGroupsSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";

import { deleteMultipleMediaAction } from "../../actions/deleteMultipleMediaAction";
import { updateMediaBatchAction } from "../../actions/updateMediaBatchAction";

type Props = {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  selectedMedia: number[];
  setSelectedMedia: Dispatch<SetStateAction<number[]>>;
};

export const MultiEdit = ({
  isOpen,
  setIsOpen,
  tags,
  sharingGroups,
  selectedMedia,
  setSelectedMedia,
}: Props) => {
  // tanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();
  const [editType, setEditType] = useState<"all" | "tag" | "sharing">("all");
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>(undefined);
  const [isUpdateConfirmOpen, setIsUpdateConfirmOpen] = useState<boolean>(false);
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();
  const updateAction = () => {
    startTransition(async () => {
      const result = await updateMediaBatchAction({
        mediaIds: selectedMedia,
        sharingGroupId: editType === "tag" ? undefined : selectedGroupId,
        tagIds: editType === "sharing" ? undefined : selectedTagIds,
      });
      if (result.success) {
        // モーダルを閉じる
        setIsUpdateConfirmOpen(false);
        // 変更メニューを閉じる
        setIsOpen(false);
        // メディアの選択状態をリセット
        setSelectedMedia([]);
        // 一覧取得クエリのキャッシュを破棄する
        queryClient.invalidateQueries({ queryKey: ["media"] });
        toast.success("メディア情報を更新しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteMultipleMediaAction({
        mediaIds: selectedMedia,
      });

      if (result.success) {
        // モーダルを閉じる
        setIsDeleteConfirmOpen(false);
        // 変更メニューを閉じる
        setIsOpen(false);
        // メディアの選択状態をリセット
        setSelectedMedia([]);
        // 一覧取得クエリのキャッシュを破棄する
        queryClient.invalidateQueries({ queryKey: ["media"] });
        toast.success("メディアをゴミ箱に移動しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const updateConfirm = () => {
    if (selectedMedia.length === 0) {
      toast.error("メディアが選択されていません");
      return;
    }
    setIsUpdateConfirmOpen(true);
  };

  const toggleMenu = () => {
    setIsOpen(!isOpen);
    setSelectedMedia([]);
  };

  return (
    <>
      <Button
        className="-mt-10 flex items-center justify-center gap-2 max-md:w-30"
        onClick={toggleMenu}
        variant={isOpen ? "cancel" : "primary"}
      >
        {!isOpen && <Image src="/images/select-start-check.svg" alt="" width={18} height={18} />}
        <p className="max-md:text-xs">{isOpen ? "閉じる" : "選択を開始"}</p>
      </Button>
      <AccordionContent isOpen={isOpen}>
        <div className="bg-background-accent border-brown-dark mt-8 rounded-xl border px-8 py-6 max-md:mt-4 max-md:px-4 max-md:pt-4">
          <p className="border-line-gray w-fit border-b pb-2 text-xl font-medium max-md:text-sm">
            選択したメディアを一括で編集する
          </p>
          <div className="mt-6 flex items-center gap-4">
            <label htmlFor="editTypeAll" className="flex items-center gap-2">
              <input
                type="radio"
                id="editTypeAll"
                className="accent-accent-pink h-4 w-4"
                checked={editType === "all"}
                onChange={() => setEditType("all")}
              />
              <p className="max-md:text-[13px]">両方編集する</p>
            </label>
            <label htmlFor="editTypeTag" className="flex items-center gap-2">
              <input
                type="radio"
                id="editTypeTag"
                className="accent-accent-pink h-4 w-4"
                checked={editType === "tag"}
                onChange={() => setEditType("tag")}
              />
              <p className="max-md:text-[13px]">タグのみ</p>
            </label>
            <label htmlFor="editTypeSharing" className="flex items-center gap-2">
              <input
                type="radio"
                id="editTypeSharing"
                className="accent-accent-pink h-4 w-4"
                checked={editType === "sharing"}
                onChange={() => setEditType("sharing")}
              />
              <p className="max-md:text-[13px]">共有範囲のみ</p>
            </label>
          </div>
          {/* タグを編集 */}
          {(editType === "all" || editType === "tag") && (
            <TagSelector
              tags={tags}
              selectedTagIds={selectedTagIds}
              onTagSelect={(tagIds) => setSelectedTagIds(tagIds)}
            />
          )}

          {/* 共有範囲を編集 */}
          {(editType === "all" || editType === "sharing") && (
            <SharingGroupsSelector
              sharingGroups={sharingGroups}
              isLoading={false}
              error={null}
              onRefresh={() => {}}
              onSharingGroupSelect={(id) => setSelectedGroupId(id)}
              selectedGroupId={selectedGroupId}
            />
          )}

          {/* ボタン */}
          <div className="mt-8 flex items-end justify-between gap-5 max-md:flex-col max-md:items-start">
            <Button variant="primary" disabled={isPending} onClick={updateConfirm}>
              変更する
            </Button>
            <button
              className="text-warning cursor-pointer text-xs underline max-md:ml-auto dark:font-medium"
              disabled={isPending}
              onClick={() => setIsDeleteConfirmOpen(true)}
            >
              選択したメディアをすべてゴミ箱に移動する
            </button>
          </div>
        </div>
      </AccordionContent>

      <ConfirmModal
        isOpen={isDeleteConfirmOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteConfirmOpen(false)}
        message="選択したメディアを一括でゴミ箱に移動します。"
        buttonType="remove"
        buttonMessage="実行する"
      />
      <ConfirmModal
        isOpen={isUpdateConfirmOpen}
        isPending={isPending}
        action={updateAction}
        closeAction={() => setIsUpdateConfirmOpen(false)}
        message={createMessage(editType)}
        buttonMessage="実行する"
      />
    </>
  );
};

const createMessage = (editType: "all" | "tag" | "sharing") => {
  switch (editType) {
    case "all":
      return "選択したメディアのタグと共有範囲を一括で更新します。";
    case "tag":
      return "選択したメディアのタグを一括で更新します。";
    case "sharing":
      return "選択したメディアの共有範囲を一括で更新します。";
    default:
      return "";
  }
};
