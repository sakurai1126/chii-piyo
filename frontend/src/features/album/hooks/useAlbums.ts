"use client";

import { useQuery } from "@tanstack/react-query";

import { AlbumResponseDto } from "@/lib/api-client/gen";

export const useAlbums = () => {
  return useQuery<AlbumResponseDto[]>({
    queryKey: ["albums"],
    queryFn: async () => {
      const res = await fetch("/api/albums");
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error ?? "アルバムの取得に失敗しました");
      }
      return res.json() as Promise<AlbumResponseDto[]>;
    },
  });
};
