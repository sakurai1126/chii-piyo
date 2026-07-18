"use client";

import { useState } from "react";

import { AccentButton } from "@/components/ui/AccentButton";
import { AddMediaModal } from "@/features/media";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

type Props = {
  isEasy: boolean;
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  albumId: number;
};
export const AddMediaAlbum = ({ isEasy, tags, sharingGroups, albumId }: Props) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);

  return (
    <>
      {/* モーダル */}
      <AddMediaModal
        isEasy={isEasy}
        tags={tags}
        sharingGroups={sharingGroups}
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        albumId={albumId}
        variant="album"
      />

      {/* 遷移ボタン */}
      <AccentButton
        className={cn("mt-10 ml-auto @max-md:mt-4", isEasy && "mx-auto @max-md:mt-8")}
        variant="button"
        onClick={() => setIsOpen(true)}
      >
        <p>アルバムにメディアを追加</p>
      </AccentButton>
    </>
  );
};
