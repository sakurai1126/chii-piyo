"use client";

import { useQuery } from "@tanstack/react-query";

import { SharingGroupResponseDto } from "@/lib/api-client/gen";
import { fetchApi } from "@/utils/fetcher";

export const useSharingGroups = () => {
  return useQuery<SharingGroupResponseDto[]>({
    queryKey: ["sharing-groups"],
    queryFn: async () => {
      // 共通化した fetchApi関数 を利用してAPIを呼び出す
      return fetchApi<SharingGroupResponseDto[]>(
        "/api/sharing-groups",
        "共有グループの取得に失敗しました",
      );
    },
  });
};
