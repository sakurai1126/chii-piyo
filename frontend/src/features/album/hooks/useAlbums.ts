"use client";

import { useQuery } from "@tanstack/react-query";

import { AlbumResponseDto } from "@/lib/api-client/gen";
import { fetchApi } from "@/utils/fetcher";

export const useAlbums = () => {
  return useQuery<AlbumResponseDto[]>({
    queryKey: ["albums"],
    queryFn: async () => {
      // 共通化した fetchApi関数 を利用してAPIを呼び出す
      return fetchApi<AlbumResponseDto[]>("/api/albums", "アルバムの取得に失敗しました");
    },
  });
};
