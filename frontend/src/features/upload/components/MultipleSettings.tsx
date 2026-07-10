"use client";

import Image from "next/image";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { DatePicker } from "@/components/ui/DatePicker";
import { AlbumSelector } from "@/features/album";
import { SharingGroupsSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";
import { AlbumResponseDto, SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";

import boxArrow from "../assets/brown-arrow.svg";
import { useMultipleSettings } from "../hooks/useMultipleSettings";
import { UploadMetadata } from "../types";

type Props = {
  tags: TagResponseDto[];
  albums: AlbumResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  updateAllMetadata: (patch: Partial<UploadMetadata>) => void;
};

export const MultipleSettings = ({ tags, albums, sharingGroups, updateAllMetadata }: Props) => {
  const { uid, isOpen, setIsOpen, selected, setSelected, handleChange, handleReset } =
    useMultipleSettings({ updateAllMetadata });
  return (
    <div className="bg-background-accent border-brown-dark mt-15 rounded-xl border px-8 pt-6 max-md:mt-8 max-md:px-4 max-md:pt-4">
      <div className="flex items-center gap-8 max-md:gap-2">
        <p className="text-xl font-medium max-md:text-sm">一括設定</p>
        <div className="flex items-center gap-2">
          <div className="bg-note-gray h-px w-5"></div>
          <p className="text-note-gray text-sm max-md:text-[10px]">
            アップロードしたファイルをまとめて設定
          </p>
        </div>
      </div>
      <AccordionContent isOpen={isOpen} id={`accordion-${uid}`}>
        {/* アルバムと日付設定 */}
        <div className="mt-8 flex gap-8 max-lg:flex-col max-md:mt-4 max-md:gap-4">
          <AlbumSelector
            albums={albums}
            onAlbumSelect={(albumId) => setSelected((prev) => ({ ...prev, albumId }))}
            selectedAlbumId={selected.albumId}
          />

          <DatePicker
            onChange={(takenAt) => setSelected((prev) => ({ ...prev, takenAt }))}
            value={selected.takenAt}
          />
        </div>
        {/* タグを編集 */}
        <TagSelector
          tags={tags}
          selectedTagIds={selected.tagIds ?? []}
          onTagSelect={(tagIds) => setSelected((prev) => ({ ...prev, tagIds }))}
          addTag={true}
        />
        {/* 共有範囲を編集 */}
        <SharingGroupsSelector
          sharingGroups={sharingGroups}
          onSharingGroupSelect={(sharingGroupId) =>
            setSelected((prev) => ({ ...prev, sharingGroupId }))
          }
          selectedGroupId={selected.sharingGroupId}
        />
        {/* ボタン */}
        <div className="mt-8 flex gap-5">
          <Button variant="cancel" onClick={handleReset}>
            リセット
          </Button>
          <Button variant="primary" onClick={() => handleChange()}>
            変更する
          </Button>
        </div>
      </AccordionContent>
      <button
        className="mx-auto grid h-10 w-full cursor-pointer place-content-center"
        aria-expanded={isOpen}
        aria-controls={`accordion-${uid}`}
        onClick={() => setIsOpen(!isOpen)}
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
