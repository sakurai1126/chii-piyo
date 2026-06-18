"use client";

import { useState } from "react";

import { AccentButton } from "@/components/ui/AccentButton";
import { AddMediaModal } from "@/features/media";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";

type Props = {
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  albumId: number;
};
export const AddMediaAlbum = ({ tags, sharingGroups, albumId }: Props) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);

  return (
    <>
      {/* モーダル */}
      <AddMediaModal
        tags={tags}
        sharingGroups={sharingGroups}
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        albumId={albumId}
      />

      {/* 遷移ボタン */}
      <AccentButton
        className="mt-10 ml-auto max-md:mt-4"
        variant="button"
        onClick={() => setIsOpen(true)}
      >
        <p>アルバムにメディアを追加する</p>
      </AccentButton>
    </>
  );
};
