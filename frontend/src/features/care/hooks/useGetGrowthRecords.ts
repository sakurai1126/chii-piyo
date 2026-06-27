"use client";

import { useQuery } from "@tanstack/react-query";

import { GrowthRecordResponseDto } from "@/lib/api-client/gen";

type Params = {
  startDate: Date;
  endDate: Date;
  initialData?: GrowthRecordResponseDto[];
};

export const useGetGrowthRecords = ({ startDate, endDate, initialData }: Params) => {
  return useQuery<GrowthRecordResponseDto[]>({
    queryKey: ["growthRecords", startDate.toISOString(), endDate.toISOString()],
    initialData, // 初期データ（今週の場合のみサーバーで取得）
    staleTime: initialData ? 1000 * 60 : 0, // 初期データが渡されている場合、1分間自動通信を防止
    queryFn: async () => {
      const res = await fetch(
        `/api/growth-records?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`,
      );
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error ?? "成長記録の取得に失敗しました");
      }
      return res.json() as Promise<GrowthRecordResponseDto[]>;
    },
  });
};
