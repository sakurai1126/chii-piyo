"use client";

import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { AccordionContent } from "@/components/ui/AccordionContent";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { SharingGroupsSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";
import { TagResponseDto } from "@/lib/api-client/gen";

import { updateMediaBatchAction } from "../../actions/updateMediaBatchAction";

// ダミーデータ

const dummySharingGroups = [
  {
    id: 1,
    name: "家族全員",
    members: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    id: 2,
    name: "夫婦のみ",
    members: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];

type Props = {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  tags: TagResponseDto[];
  selectedMedia: number[];
  setSelectedMedia: Dispatch<SetStateAction<number[]>>;
};

export const MultiEdit = ({ isOpen, setIsOpen, tags, selectedMedia, setSelectedMedia }: Props) => {
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
        setIsUpdateConfirmOpen(false);
        toast.success("メディア情報を更新しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const deleteAction = () => {
    // ゴミ箱への移動処理を後ほど実装
    setIsDeleteConfirmOpen(false);
    toast.success("メディアをゴミ箱に移動しました");
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
        <div className="bg-white-back border-brown-dark mt-8 rounded-xl border px-8 py-6 max-md:mt-4 max-md:px-4 max-md:pt-4">
          <p className="border-line-gray w-fit border-b pb-2 text-xl font-medium max-md:text-sm">
            選択したアイテムを一括で編集する
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
              sharingGroups={dummySharingGroups}
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
              className="text-warning cursor-pointer text-xs underline max-md:ml-auto"
              disabled={isPending}
              onClick={() => setIsDeleteConfirmOpen(true)}
            >
              選択したアイテムをすべてゴミ箱に移動する
            </button>
          </div>
        </div>
      </AccordionContent>
      <AnimatePresence>
        {isDeleteConfirmOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsDeleteConfirmOpen(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  選択したアイテムを一括でゴミ箱に移動します。
                  <br />
                  本当によろしいですか？
                </p>
                <div className="flex justify-center gap-5">
                  <Button
                    variant="cancel"
                    onClick={() => setIsDeleteConfirmOpen(false)}
                    disabled={isPending}
                  >
                    キャンセル
                  </Button>
                  <Button variant="remove" onClick={deleteAction} disabled={isPending}>
                    実行する
                  </Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
      <AnimatePresence>
        {isUpdateConfirmOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsUpdateConfirmOpen(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  選択したアイテムの
                  {editType === "all" && "タグと共有範囲"}
                  {editType === "tag" && "タグ"}
                  {editType === "sharing" && "共有範囲"}
                  を一括で更新します。
                  <br />
                  本当によろしいですか？
                </p>
                <div className="flex justify-center gap-5">
                  <Button
                    variant="cancel"
                    onClick={() => setIsUpdateConfirmOpen(false)}
                    disabled={isPending}
                  >
                    キャンセル
                  </Button>
                  <Button onClick={updateAction} disabled={isPending}>
                    実行する
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
