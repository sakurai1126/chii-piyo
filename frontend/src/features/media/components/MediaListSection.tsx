"use client";

import { useState } from "react";

import { MediaListResponseDto } from "@/lib/api-client/gen";

import { MediaList } from "./MediaList";
import { MultiEdit } from "./MultiEdit";

type Props = {
  initialData: MediaListResponseDto;
};

export const MediaListSection = ({ initialData }: Props) => {
  const [isSelectionMode, setIsSelectionMode] = useState(false);
  return (
    <>
      {/* 一括編集UI */}
      <MultiEdit isOpen={isSelectionMode} setIsOpen={setIsSelectionMode} />

      {/* メディア一覧 */}
      <MediaList initialData={initialData} isSelectionMode={isSelectionMode} />
    </>
  );
};
