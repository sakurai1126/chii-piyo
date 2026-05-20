"use client";

import { useQuery } from "@tanstack/react-query";

import { SharingGroupResponseDto } from "@/lib/api-client/gen";

export const useSharingGroups = () => {
  return useQuery<SharingGroupResponseDto[]>({
    queryKey: ["sharing-groups"],
    queryFn: async () => {
      const res = await fetch("/api/sharing-groups");
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error ?? "共有グループの取得に失敗しました");
      }
      return res.json() as Promise<SharingGroupResponseDto[]>;
    },
  });
};
