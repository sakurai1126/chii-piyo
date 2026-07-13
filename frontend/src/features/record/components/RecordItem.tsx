"use client";
import Image from "next/image";
import Link from "next/link";
import { Dispatch, SetStateAction, useState, useTransition } from "react";

import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import {
  FirstRecordResponseDto,
  SharingGroupResponseDto,
  TagResponseDto,
  WordRecordResponseDto,
} from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";
import {
  calculateDaysSinceBirth,
  formatJapaneseDateBasic,
  formatJapaneseDateNonTime,
} from "@/utils/date";

import { deleteFirstRecordAction } from "../actions/deleteFirstRecordAction";
import { deleteWordRecordAction } from "../actions/deleteWordRecordAction";
import { RecordData } from "../types";

import { RecordEditMenu } from "./RecordEditMenu";

// 誕生日指定
const birthday = new Date("2025-08-06");

type Props = {
  isAdmin: boolean;
  index: number;
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
} & (
  | { variant: "first"; item: FirstRecordResponseDto }
  | { variant: "word"; item: WordRecordResponseDto }
);

export const RecordItem = ({ isAdmin, item, index, tags, sharingGroups, variant }: Props) => {
  const [isEditMode, setIsEditMode] = useState<boolean>(false);

  // 編集時の初期値データ
  const initialEditData: RecordData = {
    id: item.id,
    title: item.title,
    recordedDate: formatJapaneseDateBasic(new Date(item.recordedDate)),
    comment: item.comment,
    media: item.media.map((media) => ({
      id: media.id,
      url: media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png",
    })),
  };

  return (
    <div className="relative flex items-center gap-10 py-2.5 @max-md:gap-6 @max-md:py-2">
      {variant === "first" && (
        <>
          <div className="bg-brown-dark h-[9px] w-[9px] shrink-0 rounded-full"></div>
          <div
            className={cn(
              "bg-brown-dark absolute left-1 h-full w-px",
              index === 0 && "top-[50%] h-[50%]",
            )}
          ></div>
        </>
      )}
      <div className="bg-background-normal dark:bg-background-accent border-brown-dark w-full rounded-lg border px-6 pt-6 pb-4 @max-md:p-3">
        {/* 通常表示 */}
        {!isEditMode && (
          <RecordItemDisplayMode
            isAdmin={isAdmin}
            item={item}
            setIsEditMode={setIsEditMode}
            variant={variant}
          />
        )}
        {isEditMode && (
          <RecordEditMenu
            tags={tags}
            sharingGroups={sharingGroups}
            setIsMenuOpen={setIsEditMode}
            initialEditData={initialEditData}
            variant={variant === "first" ? "editFirstRecord" : "editWordRecord"}
          />
        )}
      </div>
    </div>
  );
};

/**
 * 記録の通常表示
 */
const RecordItemDisplayMode = ({
  isAdmin,
  item,
  variant,
  setIsEditMode,
}: {
  isAdmin: boolean;
  item: FirstRecordResponseDto | WordRecordResponseDto;
  variant: "first" | "word";
  setIsEditMode: Dispatch<SetStateAction<boolean>>;
}) => {
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 削除確認モーダルの状態
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState<boolean>(false);

  // 削除処理
  const deleteAction = () => {
    startTransition(async () => {
      if (variant === "first") {
        const result = await deleteFirstRecordAction({ id: item.id });

        if (result.success) {
          setIsDeleteConfirmOpen(false);
          toast.success("はじめて記録の削除に成功しました");
        } else {
          toast.error(result.error);
        }
      }

      if (variant === "word") {
        const result = await deleteWordRecordAction({ id: item.id });

        if (result.success) {
          setIsDeleteConfirmOpen(false);
          toast.success("ことばの記録の削除に成功しました");
        } else {
          toast.error(result.error);
        }
      }
    });
  };

  return (
    <>
      <div className="flex items-center @max-md:flex-col @max-md:items-start">
        <p className="text-2xl font-medium @max-md:text-lg">{item.title}</p>
        <span className="bg-line-gray mr-3 ml-6 h-px w-6 @max-md:hidden"></span>
        <p className="text-note-gray text-sm @max-md:mt-1">
          {formatJapaneseDateNonTime(item.recordedDate)}&emsp;
          {calculateDaysSinceBirth(birthday, item.recordedDate)}目
        </p>
      </div>
      <p className="mt-5 @max-md:mt-3 @max-md:text-sm">{item.comment}</p>
      <div className="mt-5 flex flex-wrap gap-3 @max-md:gap-2">
        {item.media.map((media) => (
          <Link
            href={`/media/${media.id}`}
            className="transition-all hover:opacity-70"
            key={media.id}
            scroll={false}
          >
            <Image
              src={media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
              alt=""
              className="rounded-sm @max-md:h-12 @max-md:w-12"
              width={80}
              height={80}
            />
          </Link>
        ))}
      </div>
      {isAdmin && (
        <div className="mt-3 ml-auto flex w-fit gap-3">
          <button
            className="cursor-pointer underline transition-all hover:opacity-70 @max-md:text-xs"
            onClick={() => setIsEditMode(true)}
          >
            編集
          </button>
          <button
            className="text-warning cursor-pointer underline transition-all hover:opacity-70 @max-md:text-xs dark:font-medium"
            onClick={() => setIsDeleteConfirmOpen(true)}
          >
            削除
          </button>
        </div>
      )}

      {/* 削除確認モーダル */}
      <ConfirmModal
        isOpen={isDeleteConfirmOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteConfirmOpen(false)}
        message="選択した記録を削除します。"
        buttonType="remove"
        buttonMessage="削除する"
      />
    </>
  );
};
