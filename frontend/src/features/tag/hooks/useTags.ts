"use client";

import { useQuery } from "@tanstack/react-query";

import type { TagResponseDto } from "@/lib/api-client/gen";

export const useTags = () => {
  return useQuery<TagResponseDto[]>({
    queryKey: ["tags"],
    queryFn: async () => {
      const res = await fetch("/api/tags");
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error ?? "タグの取得に失敗しました");
      }
      return res.json() as Promise<TagResponseDto[]>;
    },
  });
};
