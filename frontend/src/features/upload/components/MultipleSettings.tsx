import Image from "next/image";
import { useId, useState } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { DatePicker } from "@/components/ui/DatePicker";
import { AlbumSelector } from "@/features/album";
import { AlbumAddForm } from "@/features/album/components/AlbumAddForm";
import { UseAlbumsResult } from "@/features/album/types";
import { SharingGroupsSelector } from "@/features/sharing";
import { UseSharingGroupsResult } from "@/features/sharing/types";
import { TagSelector } from "@/features/tag";
import { UseTagsResult } from "@/features/tag/types";

import boxArrow from "../assets/brown-arrow.svg";

type Props = {
  tagsState: UseTagsResult;
  albumsState: UseAlbumsResult;
  sharingGroupsState: UseSharingGroupsResult;
};

export const MultipleSettings = ({ tagsState, albumsState, sharingGroupsState }: Props) => {
  const uid = useId();
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="bg-white-back border-brown-dark mt-15 rounded-xl border px-8 pt-6 max-md:mt-8 max-md:px-4 max-md:pt-4">
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
          <div>
            <AlbumSelector
              albums={albumsState.albums}
              isLoading={albumsState.isLoading}
              error={albumsState.error}
              onRefresh={albumsState.refetch}
              // TODO 次回実装
              onAlbumSelect={(albumId) => albumId}
            />
            <AlbumAddForm onAlbumCreated={albumsState.refetch} />
          </div>

          <DatePicker
            // TODO 次回実装
            onChange={(date) => date}
          />
        </div>
        {/* タグを編集 */}
        <TagSelector
          tags={tagsState.tags}
          isLoading={tagsState.isLoading}
          error={tagsState.error}
          onRefresh={tagsState.refetch}
          // TODO 次回実装
          selectedTagIds={[]}
          onTagSelect={(tagIds) => tagIds}
        />
        {/* 共有範囲を編集 */}
        <SharingGroupsSelector
          sharingGroups={sharingGroupsState.sharingGroups}
          isLoading={sharingGroupsState.isLoading}
          error={sharingGroupsState.error}
          onRefresh={sharingGroupsState.refetch}
          // TODO 次回実装
          onSharingGroupSelect={(sharingGroupId) => sharingGroupId}
        />
        {/* ボタン */}
        <div className="mt-8 flex gap-5">
          <Button variant="cancel">キャンセル</Button>
          <Button variant="primary">変更する</Button>
        </div>
      </AccordionContent>
      <button
        className="mx-auto grid h-10 w-full cursor-pointer place-content-center"
        aria-expanded={isOpen}
        aria-controls={`accordion-${uid}`}
        // TODO 次回実装
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
