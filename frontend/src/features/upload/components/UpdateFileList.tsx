"use client";

import { Button } from "@/components/ui/Button";
import { AlbumResponseDto, SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";

import { UploadMedia, UploadMetadata } from "../types";

import { UploadFile } from "./UploadFile";

type Props = {
  isAdmin: boolean;
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
    <div className="mt-15 max-md:mt-10">
      <div className="flex items-start justify-between">
        <p className="text-xl font-medium max-md:text-sm">アップロードするファイル</p>
        <p className="text-note-gray pt-0.5 text-right max-md:text-xs">
          写真 : {totalImageCount}枚 + 動画 : {totalVideoCount}本
          <br />
          合計サイズ :{" "}
          {totalSizeInKB > 1024 ? `${totalSizeInMB.toFixed(1)}MB` : `${totalSizeInKB.toFixed(0)}KB`}
        </p>
      </div>
      <div className="mt-5 grid gap-5">
        {/* 各ファイル */}
        {items.map((item, index) => (
          <UploadFile
            key={item.id}
            isAdmin={isAdmin}
            item={item}
            onRemove={() => onRemove(index)}
            tags={tags}
            albums={albums}
            sharingGroups={sharingGroups}
            updateItemMetadata={updateItemMetadata}
          />
        ))}
      </div>
      <div className="border-t-line-gray mt-15 flex items-center justify-between border-t pt-10">
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
