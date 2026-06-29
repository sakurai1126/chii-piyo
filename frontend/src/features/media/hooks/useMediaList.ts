"use client";

import { useQuery } from "@tanstack/react-query";

import { GetMediaListMediaKindEnum, MediaListResponseDto } from "@/lib/api-client/gen";
import { fetchApi } from "@/utils/fetcher";

export type UseMediaListParams = {
  offset?: number;
  limit?: number;
  mediaKind?: GetMediaListMediaKindEnum;
  albumId?: number;
  tagId?: number;
  sharingGroupId?: number;
  startDate?: Date;
  endDate?: Date;
};

export const useMediaList = (params: UseMediaListParams = {}) => {
  return useQuery<MediaListResponseDto>({
    queryKey: ["media", "list", params],
    queryFn: async () => {
      // 受け取ったパラメータでクエリパラメータを生成
      const sp = new URLSearchParams();
      if (params.offset !== undefined) sp.set("offset", String(params.offset));
      if (params.limit !== undefined) sp.set("limit", String(params.limit));
      if (params.mediaKind) sp.set("mediaKind", params.mediaKind);
      if (params.albumId !== undefined) sp.set("albumId", String(params.albumId));
      if (params.tagId !== undefined) sp.set("tagId", String(params.tagId));
      if (params.sharingGroupId !== undefined)
        sp.set("sharingGroupId", String(params.sharingGroupId));
      if (params.startDate) sp.set("startDate", params.startDate.toISOString().slice(0, 10));
      if (params.endDate) sp.set("endDate", params.endDate.toISOString().slice(0, 10));

      // 共通化した fetchApi関数 を利用してAPIを呼び出す
      return fetchApi<MediaListResponseDto>(
        `/api/media?${sp.toString()}`,
        "メディアの取得に失敗しました",
      );
    },
  });
};
