"use client";

import Image from "next/image";
import { useEffect, useId, useState } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { DatePicker } from "@/components/ui/DatePicker";
import { toast } from "@/components/ui/Toast";
import { AlbumSelector } from "@/features/album";
import { SharingGroupsSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";
import { AlbumResponseDto, SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import boxArrow from "../assets/brown-arrow.svg";
import { UploadMedia, UploadMetadata, UploadStatus } from "../types";

type Props = {
  isAdmin: boolean;
  isEasy: boolean;
  item: UploadMedia;
  onRemove: () => void;
  tags: TagResponseDto[];
  albums: AlbumResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  updateItemMetadata: (itemId: string, patch: Partial<UploadMetadata>) => void;
};

export const UploadFile = ({
  isAdmin,
  isEasy,
  item,
  onRemove,
  tags,
  albums,
  sharingGroups,
  updateItemMetadata,
}: Props) => {
  const uid = useId();
  // アコーディオンの開閉状態
  const [isOpen, setIsOpen] = useState(false);
  // ファイルサイズの表示 (KB or MB)
  const sizeInKB = item.file.size / 1024;
  const sizeInMB = sizeInKB / 1024;

  // 進捗バーを表示する条件
  // creating/uploading/completing 中のみ表示する
  const showProgress =
    item.status === "creating" || item.status === "uploading" || item.status === "completing";

  // アップロード中・完了後は削除と編集を不可にする
  const isLocked = item.status !== "idle" && item.status !== "failed";

  const handleMetadataChange = (patch: Partial<UploadMetadata>) => {
    updateItemMetadata(item.id, patch);
  };

  // 動画かどうかの判定
  const isVideo = item.file.type.startsWith("video/");

  // エラーメッセージの通知
  useEffect(() => {
    if (item.errorMessage) {
      toast.error(item.errorMessage);
    }
  }, [item.errorMessage]);

  return (
    <div className="bg-background-normal border-brown-dark rounded-xl border px-5 pt-5">
      <div
        className={cn(
          "flex items-start gap-8 @max-md:gap-3",
          isEasy && "items-center @max-md:flex-col",
        )}
      >
        {isVideo ? (
          <video
            src={`${item.previewUrl}#t=0.1`}
            className={cn("h-30 w-30 rounded-lg object-cover", isEasy && "h-50 w-50")}
            muted
            playsInline
            preload="metadata"
          />
        ) : (
          <Image
            src={`${item.previewUrl}#t=0.1`} // 0.1秒時点を指定
            alt=""
            width={120}
            height={120}
            className={cn(
              "h-30 w-30 rounded-lg object-cover @max-md:h-20 @max-md:w-20",
              isEasy && "@max-md:h-50 @max-md:w-50",
            )}
            unoptimized
          />
        )}
        <div className="w-full">
          <div
            className={cn(
              "flex h-30 items-start justify-between gap-5 @max-md:flex-col",
              isEasy && "h-auto",
            )}
          >
            <div hidden={isEasy}>
              <p className="break-all @max-md:text-[13px]">
                {item.file.name.length > 100
                  ? `${item.file.name.slice(0, 100)}...`
                  : item.file.name}
              </p>
              <p className="mt-2 text-[13px] @max-md:text-[11px]">
                {sizeInKB > 1024 ? `${sizeInMB.toFixed(1)}MB` : `${sizeInKB.toFixed(0)}KB`}{" "}
                {item.width && item.height && `${item.width} × ${item.height}`}
              </p>
              {/* ステータスバッジ */}
              {item.status !== "idle" && (
                <div className="mt-2 flex items-center gap-2">
                  <span
                    className={cn(
                      "inline-block rounded-2xl px-3 py-0.5 text-xs",
                      STATUS_BADGE_CLASS[item.status],
                    )}
                  >
                    {STATUS_LABEL[item.status]}
                  </span>
                  {item.status === "uploading" && (
                    <span className="text-note-gray text-xs">{item.progress}%</span>
                  )}
                </div>
              )}
            </div>

            {/* 削除ボタン - アップロード中は非表示 */}
            {!isLocked && (
              <button
                className={cn(
                  "text-warning shrink-0 cursor-pointer text-xs underline transition-all hover:opacity-70 @max-md:ml-auto dark:font-medium",
                  isEasy && "text-[13px] @max-md:mx-auto",
                )}
                onClick={onRemove}
                type="button"
              >
                {isVideo ? "この動画を削除する" : "この画像を削除する"}
              </button>
            )}
          </div>
          {/* 進捗バー */}
          {showProgress && (
            <div className="bg-line-gray mt-2 h-1 w-full overflow-hidden rounded-full">
              <div
                className="bg-brown-middle h-full transition-all duration-200"
                style={{ width: `${item.progress}%` }}
              />
            </div>
          )}{" "}
        </div>
      </div>

      {/* メタデータ更新メニュー */}
      <AccordionContent isOpen={isOpen} id={`accordion-${uid}`}>
        <div className="border-t-line-gray ml-37.5 border-t border-dashed pb-3 @max-md:mt-5 @max-md:ml-0">
          {/* コメント */}
          <p
            className={cn("mt-6 @max-md:text-[13px]", isEasy && "font-medium @max-md:text-[16px]")}
          >
            コメント
          </p>
          <textarea
            name={`comment-${uid}`}
            className={cn(
              "border-line-gray focus:outline-brown-light bg-light-dark mt-2 h-20 w-full max-w-172.5 rounded-sm border p-3 @max-md:h-18 dark:outline-none",
              isEasy && "@max-md:h-25",
            )}
            disabled={isLocked}
            onChange={(e) => handleMetadataChange({ comment: e.target.value })}
          />
          {/* アルバムと日付設定 */}
          <div className="mt-8 flex gap-8 @max-lg:flex-col @max-md:mt-4 @max-md:gap-4">
            <AlbumSelector
              isAdmin={isAdmin}
              isEasy={isEasy}
              albums={albums}
              onAlbumSelect={(albumId) =>
                handleMetadataChange({ albumId: albumId ? Number(albumId) : undefined })
              }
              selectedAlbumId={item.metadata.albumId}
            />

            <DatePicker
              isEasy={isEasy}
              value={item.metadata.takenAt}
              onChange={(date) => handleMetadataChange({ takenAt: date })}
            />
          </div>
          {!isEasy && (
            <>
              {/* タグを編集 */}
              <TagSelector
                tags={tags}
                selectedTagIds={item.metadata.tagIds ?? []}
                onTagSelect={(tagIds) => handleMetadataChange({ tagIds })}
                addTag={isAdmin}
              />
              {/* 共有範囲を編集 */}
              <SharingGroupsSelector
                sharingGroups={sharingGroups}
                onSharingGroupSelect={(sharingGroupId) => handleMetadataChange({ sharingGroupId })}
                selectedGroupId={item.metadata.sharingGroupId}
              />{" "}
            </>
          )}
        </div>
      </AccordionContent>

      {/* アコーディオン開閉ボタン */}
      <button
        className="mx-auto grid h-10 w-full cursor-pointer place-content-center"
        aria-expanded={isOpen}
        aria-controls={`accordion-${uid}`}
        onClick={() => setIsOpen(!isOpen)}
        type="button"
      >
        <Image
          src={boxArrow}
          alt=""
          width={13}
          height={7}
          className={cn("transition-transform duration-300", isOpen && "rotate-180")}
        />
      </button>
    </div>
  );
};

// アップロード状態に応じた表示ラベル
const STATUS_LABEL: Record<UploadStatus, string> = {
  idle: "",
  creating: "準備中...",
  uploading: "アップロード中",
  completing: "仕上げ中...",
  completed: "完了",
  failed: "失敗",
};

// アップロード状態に応じた表示色クラス
const STATUS_BADGE_CLASS: Record<UploadStatus, string> = {
  idle: "",
  creating: "bg-background-accent text-brown-middle",
  uploading: "bg-background-accent text-brown-middle",
  completing: "bg-background-accent text-brown-middle",
  completed: "bg-success-back text-success",
  failed: "bg-accent-pink-back text-accent-pink",
};
