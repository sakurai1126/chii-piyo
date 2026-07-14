"use client";

import { Button } from "@/components/ui/Button";
import { AlbumResponseDto, SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import { UploadMedia, UploadMetadata } from "../types";

import { UploadFile } from "./UploadFile";

type Props = {
  isAdmin: boolean;
  isEasy: boolean;
  items: UploadMedia[];
  onRemove: (index: number) => void;
  onRemoveAll: () => void;
  onUpload: () => void;
  isUploading: boolean;
  tags: TagResponseDto[];
  albums: AlbumResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  updateItemMetadata: (itemId: string, patch: Partial<UploadMetadata>) => void;
};

export const UpdateFileList = ({
  isAdmin,
  isEasy,
  items,
  onRemove,
  onRemoveAll,
  onUpload,
  isUploading,
  tags,
  albums,
  sharingGroups,
  updateItemMetadata,
}: Props) => {
  const totalSizeInKB = items.reduce((total, item) => total + item.file.size / 1024, 0);
  const totalSizeInMB = totalSizeInKB / 1024;

  // アップロード対象のみを抽出
  const targets = items.filter((item) => item.status === "idle" || item.status === "failed");

  // 写真と動画の総件数をカウント
  const totalImageCount = items.filter((item) => item.file.type.startsWith("image/")).length;
  const totalVideoCount = items.filter((item) => item.file.type.startsWith("video/")).length;

  // 対象の中での写真・動画の件数
  const targetImageCount = targets.filter((item) => item.file.type.startsWith("image/")).length;
  const targetVideoCount = targets.filter((item) => item.file.type.startsWith("video/")).length;

  return (
    <div className="mt-15 @max-md:mt-10">
      <div
        className={cn("flex items-start justify-between", isEasy && "flex-col items-center gap-3")}
      >
        <p className={cn("text-xl font-medium @max-md:text-sm", isEasy && "@max-md:text-[18px]")}>
          アップロードするファイル
        </p>
        <p
          className={cn(
            "pt-0.5 text-right @max-md:text-xs",
            isEasy ? "@max-md:text-[16px]" : "text-note-gray",
          )}
        >
          写真 : {totalImageCount}枚 + 動画 : {totalVideoCount}本
          <span hidden={isEasy}>
            <br />
            合計サイズ :{" "}
            {totalSizeInKB > 1024
              ? `${totalSizeInMB.toFixed(1)}MB`
              : `${totalSizeInKB.toFixed(0)}KB`}
          </span>
        </p>
      </div>
      <div className="mt-5 grid gap-5">
        {/* 各ファイル */}
        {items.map((item, index) => (
          <UploadFile
            key={item.id}
            isAdmin={isAdmin}
            isEasy={isEasy}
            item={item}
            onRemove={() => onRemove(index)}
            tags={tags}
            albums={albums}
            sharingGroups={sharingGroups}
            updateItemMetadata={updateItemMetadata}
          />
        ))}
      </div>
      <div
        className={cn(
          "border-t-line-gray mt-15 flex items-center justify-between border-t pt-10 @max-md:flex-col @max-md:gap-6",
          isEasy && "font-medium",
        )}
      >
        <p>
          {targetImageCount > 0 &&
            targetVideoCount > 0 &&
            `写真${targetImageCount}枚と動画${targetVideoCount}本`}
          {targetImageCount > 0 && targetVideoCount === 0 && `写真${targetImageCount}枚`}
          {targetVideoCount > 0 && targetImageCount === 0 && `動画${targetVideoCount}本`}
          {(targetImageCount > 0 || targetVideoCount > 0) && `をアップロードします`}
        </p>
        <div className="flex gap-5">
          {!isUploading && (
            <Button variant="cancel" onClick={onRemoveAll}>
              キャンセル
            </Button>
          )}

          <Button
            isEasy={isEasy}
            variant="primary"
            onClick={onUpload}
            disabled={isUploading || targets.length === 0}
            disabledStyle={isUploading || targets.length === 0}
          >
            {isUploading ? "アップロード中..." : "アップロード"}
          </Button>
        </div>
      </div>
    </div>
  );
};
