import Image from "next/image";
import { useId, useState } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { DatePicker } from "@/components/ui/DatePicker";
import { AlbumSelector } from "@/features/album";
import { UseAlbumsResult } from "@/features/album/types";
import { SharingGroupsSelector } from "@/features/sharing";
import { UseSharingGroupsResult } from "@/features/sharing/types";
import { TagSelector } from "@/features/tag";
import { UseTagsResult } from "@/features/tag/types";

import boxArrow from "../assets/brown-arrow.svg";
import { UploadImage, UploadStatus } from "../types";

type Props = {
  item: UploadImage;
  onRemove: () => void;
  tagsState: UseTagsResult;
  albumsState: UseAlbumsResult;
  sharingGroupsState: UseSharingGroupsResult;
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
  creating: "bg-brown-back text-brown-middle",
  uploading: "bg-brown-back text-brown-middle",
  completing: "bg-brown-back text-brown-middle",
  completed: "bg-success-back text-success",
  failed: "bg-accent-pink-back text-accent-pink",
};

export const UploadFile = ({
  item,
  onRemove,
  tagsState,
  albumsState,
  sharingGroupsState,
}: Props) => {
  const uid = useId();
  const [isOpen, setIsOpen] = useState(false);
  const sizeInKB = item.file.size / 1024;
  const sizeInMB = sizeInKB / 1024;

  // 進捗バーを表示する条件
  // creating/uploading/completing 中のみ表示する
  const showProgress =
    item.status === "creating" || item.status === "uploading" || item.status === "completing";

  // アップロード中・完了後は削除と編集を不可にする
  const isLocked = item.status !== "idle" && item.status !== "failed";

  return (
    <div className="bg-white-back border-brown-dark rounded-xl border px-5 pt-5">
      <div className="flex items-start gap-8 max-md:gap-3">
        <Image
          src={item.previewUrl}
          alt=""
          width={120}
          height={120}
          className="h-30 w-30 rounded-lg object-cover"
          unoptimized
        />
        <div className="w-full">
          <div className="flex h-30 items-start justify-between max-md:flex-col">
            <div>
              <p className="max-md:text-[13px]">{item.file.name}</p>
              <p className="mt-2 text-[13px] max-md:text-[11px]">
                {sizeInKB > 1024 ? `${sizeInMB.toFixed(1)}MB` : `${sizeInKB.toFixed(0)}KB`}{" "}
                {item.width && item.height && `${item.width} × ${item.height}`}
              </p>
              {/* ステータスバッジ */}
              {item.status !== "idle" && (
                <div className="mt-2 flex items-center gap-2">
                  <span
                    className={`inline-block rounded-2xl px-3 py-0.5 text-xs ${STATUS_BADGE_CLASS[item.status]}`}
                  >
                    {STATUS_LABEL[item.status]}
                  </span>
                  {item.status === "uploading" && (
                    <span className="text-note-gray text-xs">{item.progress}%</span>
                  )}
                </div>
              )}
              {/* エラーメッセージ */}
              {item.status === "failed" && item.errorMessage && (
                <p className="text-warning mt-1 text-xs">{item.errorMessage}</p>
              )}{" "}
            </div>

            {/* 削除ボタンはアップロード中以外のみ表示 */}
            {!isLocked && (
              <button
                className="text-warning hover:text-warning-hover cursor-pointer text-xs underline transition-all duration-400 max-md:ml-auto"
                onClick={onRemove}
                type="button"
              >
                この画像を削除する
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

      <AccordionContent isOpen={isOpen} id={`accordion-${uid}`}>
        <div className="border-t-line-gray ml-37.5 border-t border-dashed pb-3 max-md:mt-5 max-md:ml-0">
          {/* コメント */}
          <p className="mt-6 max-md:text-[13px]">コメント</p>
          <textarea
            name={`comment-${uid}`}
            className="border-line-gray focus:outline-brown-light mt-2 h-20 w-full max-w-172.5 rounded-sm border bg-white p-3 max-md:h-18"
            disabled={isLocked}
          />
          {/* アルバムと日付設定 */}
          <div className="mt-8 flex gap-8 max-lg:flex-col max-md:mt-4 max-md:gap-4">
            <AlbumSelector
              albums={albumsState.albums}
              isLoading={albumsState.isLoading}
              error={albumsState.error}
              onRefresh={albumsState.refetch}
            />
            <DatePicker />
          </div>
          {/* タグを編集 */}
          <TagSelector
            tags={tagsState.tags}
            isLoading={tagsState.isLoading}
            error={tagsState.error}
            onRefresh={tagsState.refetch}
          />
          {/* 共有範囲を編集 */}
          <SharingGroupsSelector
            sharingGroups={sharingGroupsState.sharingGroups}
            isLoading={sharingGroupsState.isLoading}
            error={sharingGroupsState.error}
            onRefresh={sharingGroupsState.refetch}
          />
        </div>
      </AccordionContent>

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
          className={`${isOpen ? "rotate-180" : ""} transition-transform duration-300`}
        />
      </button>
    </div>
  );
};
