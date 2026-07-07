"use client";

import Image from "next/image";
import { useState, useTransition } from "react";

import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { TagResponseDto } from "@/lib/api-client/gen";

import { createTagAction } from "../actions/createTagAction";
import { deleteTagAction } from "../actions/deleteTagAction";
import { updateTagAction } from "../actions/updateTagAction";
import backIcon from "../assets/back.svg";
import deleteIcon from "../assets/delete.svg";
import editIcon from "../assets/edit.svg";
import plusIcon from "../assets/plus.svg";
import saveIcon from "../assets/save.svg";

type Props = {
  tags: TagResponseDto[];
};

export const SettingsTags = ({ tags }: Props) => {
  return (
    <div className="bg-background-light border-brown-dark mt-4 rounded-lg border px-7.5 py-6 max-md:mt-3 max-md:p-5">
      <div className="flex flex-wrap items-center gap-x-4 gap-y-3">
        {tags.map((tag) => (
          <TagItem key={tag.id} tag={tag} />
        ))}
        <NewTag />
      </div>
    </div>
  );
};

const TagItem = ({ tag }: { tag: TagResponseDto }) => {
  const [isEditMode, setIsEditMode] = useState<boolean>(false);
  const [isDeleteConfirm, setIsDeleteConfirm] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  const [newTagName, setNewTagName] = useState<string>(tag.name);

  // タグを更新する
  const saveUpdateAction = () => {
    if (!newTagName.trim()) {
      toast.error("タグ名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await updateTagAction({
        tagId: tag.id,
        name: newTagName,
      });

      if (result.success) {
        setIsEditMode(false);
        toast.success("タグの編集に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  // タグを削除する
  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteTagAction({
        tagId: tag.id,
      });

      if (result.success) {
        setIsDeleteConfirm(false);
        toast.success("タグの削除に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const cancelEdit = () => {
    setNewTagName(tag.name);
    setIsEditMode(false);
  };

  return (
    <>
      <div className="bg-accent-orange-back border-brown-middle flex h-8 items-center rounded-2xl border pl-4">
        {isEditMode ? (
          <input
            type="text"
            className="text-brown-dark h-5 border-b text-sm outline-none"
            value={newTagName}
            onChange={(e) => setNewTagName(e.target.value)}
          />
        ) : (
          <p className="text-brown-dark text-sm max-md:text-xs">{newTagName}</p>
        )}

        <p className="bg-brown-dark mr-3 ml-2 rounded-2xl px-1 text-[10px] text-white">
          {tag.mediaCount}
        </p>
        {isEditMode ? (
          <>
            <button
              className="border-brown-middle/30 hover:bg-brown-dark/10 grid aspect-square h-8 cursor-pointer place-content-center border-x transition-all duration-400"
              onClick={cancelEdit}
              disabled={isPending}
            >
              <Image src={backIcon} alt="" width={11} height={11} />
            </button>
            <button
              className="hover:bg-warning/10 grid aspect-square h-8 cursor-pointer place-content-center rounded-r-2xl transition-all duration-400"
              onClick={saveUpdateAction}
              disabled={isPending}
            >
              <Image src={saveIcon} alt="" width={15} height={15} />
            </button>
          </>
        ) : (
          <>
            <button
              className="border-brown-middle/30 hover:bg-brown-dark/10 grid aspect-square h-8 cursor-pointer place-content-center border-x transition-all duration-400"
              onClick={() => setIsEditMode(true)}
              disabled={isPending}
            >
              <Image src={editIcon} alt="" width={11} height={11} />
            </button>
            <button
              className="hover:bg-warning/10 grid aspect-square h-8 cursor-pointer place-content-center rounded-r-2xl transition-all duration-400"
              onClick={() => setIsDeleteConfirm(true)}
              disabled={isPending}
            >
              <Image src={deleteIcon} alt="" width={10} height={10} />
            </button>
          </>
        )}
      </div>
      <ConfirmModal
        isOpen={isDeleteConfirm}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteConfirm(false)}
        message={`タグ【${tag.name}】を削除します。`}
        buttonType="remove"
        buttonMessage="削除する"
      />
    </>
  );
};

const NewTag = () => {
  const [isEditMode, setIsEditMode] = useState<boolean>(false);
  const [newTagName, setNewTagName] = useState<string>("");
  const [isPending, startTransition] = useTransition();

  // タグを作成する
  const createAction = () => {
    if (!newTagName.trim()) {
      toast.error("タグ名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await createTagAction({
        name: newTagName,
      });

      if (result.success) {
        setIsEditMode(false);
        setNewTagName("");
        toast.success("タグの作成に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  const cancelEdit = () => {
    setNewTagName("");
    setIsEditMode(false);
  };

  return isEditMode ? (
    <div className="bg-accent-orange-back border-brown-middle flex h-8 items-center rounded-2xl border pl-4">
      <input
        type="text"
        className="text-brown-dark mr-4 h-5 border-b text-sm outline-none"
        value={newTagName}
        onChange={(e) => setNewTagName(e.target.value)}
      />
      <button
        className="border-brown-middle/30 hover:bg-brown-dark/10 grid aspect-square h-8 cursor-pointer place-content-center border-x transition-all duration-400"
        onClick={cancelEdit}
        disabled={isPending}
      >
        <Image src={backIcon} alt="" width={11} height={11} />
      </button>
      <button
        className="hover:bg-warning/10 grid aspect-square h-8 cursor-pointer place-content-center rounded-r-2xl transition-all duration-400"
        disabled={isPending}
        onClick={createAction}
      >
        <Image src={saveIcon} alt="" width={15} height={15} />
      </button>
    </div>
  ) : (
    <button
      className="border-line-gray hover:bg-line-gray/30 bg-light-dark flex h-8 cursor-pointer items-center gap-1.5 rounded-3xl border border-dashed px-4 transition-all hover:border-solid"
      onClick={() => setIsEditMode(true)}
      disabled={isPending}
    >
      <p className="text-xs">新規</p>
      <Image src={plusIcon} alt="" width={12} height={12} />
    </button>
  );
};
