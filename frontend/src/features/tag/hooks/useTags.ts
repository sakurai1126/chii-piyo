"use client";

import { useQuery } from "@tanstack/react-query";

import type { TagResponseDto } from "@/lib/api-client/gen";
import { fetchApi } from "@/utils/fetcher";

export const useTags = () => {
  return useQuery<TagResponseDto[]>({
    queryKey: ["tags"],
    queryFn: async () => {
      // 共通化した fetchApi関数 を利用してAPIを呼び出す
      return fetchApi<TagResponseDto[]>("/api/tags", "タグの取得に失敗しました");
    },
  });
};
