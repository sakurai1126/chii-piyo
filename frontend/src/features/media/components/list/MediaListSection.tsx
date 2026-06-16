"use client";

import { useSearchParams } from "next/navigation";
import { useMemo, useState } from "react";

import {
  GetMediaListMediaKindEnum,
  MediaListResponseDto,
  SharingGroupResponseDto,
  TagResponseDto,
  UserResponseDto,
} from "@/lib/api-client/gen";

import { MediaList } from "./MediaList";
import { MultiEdit } from "./MultiEdit";

type Props = {
  initialData: MediaListResponseDto;
  users: UserResponseDto[];
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  albumId?: number;
};

export const MediaListSection = ({ initialData, users, tags, sharingGroups, albumId }: Props) => {
  const [isSelectionMode, setIsSelectionMode] = useState(false);
  const [selectedMedia, setSelectedMedia] = useState<number[]>([]);
  // 例: URLのsearchParamsから構築
  const sp = useSearchParams();

  let albumIdParam: number | undefined = undefined;
  if (albumId) {
    albumIdParam = albumId;
  } else if (sp.get("albumId")) {
    albumIdParam = Number(sp.get("albumId"));
  }

  // useMemoでクエリパラメータの変更時のみparamsを再計算
  const params = useMemo(
    () => ({
      mediaKind:
        sp.get("mediaKind") == "VIDEO" || sp.get("mediaKind") == "PHOTO"
          ? (sp.get("mediaKind") as GetMediaListMediaKindEnum)
          : undefined,
      albumId: albumIdParam,
      tagId: sp.getAll("tagId").map(Number),
      sharingGroupId: sp.get("sharingGroupId") ? Number(sp.get("sharingGroupId")) : undefined,
      startDate: sp.get("startDate") ? new Date(sp.get("startDate") as string) : undefined,
      endDate: sp.get("endDate") ? new Date(sp.get("endDate") as string) : undefined,
    }),
    [sp, albumIdParam],
  );

  return (
    <>
      {/* 一括編集UI */}
      <MultiEdit
        isOpen={isSelectionMode}
        setIsOpen={setIsSelectionMode}
        tags={tags}
        sharingGroups={sharingGroups}
        selectedMedia={selectedMedia}
        setSelectedMedia={setSelectedMedia}
      />

      {/* メディア一覧 */}
      <MediaList
        initialData={initialData}
        isSelectionMode={isSelectionMode}
        params={params}
        users={users}
        selectedMedia={selectedMedia}
        setSelectedMedia={setSelectedMedia}
      />
    </>
  );
};
