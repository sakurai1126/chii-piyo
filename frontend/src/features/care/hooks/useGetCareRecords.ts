"use client";

import { useQuery } from "@tanstack/react-query";

import { CareRecordListResponseDto } from "@/lib/api-client/gen";

type Params = {
  startDate: Date;
  endDate: Date;
  initialData?: CareRecordListResponseDto;
};

export const useGetCareRecords = ({ startDate, endDate, initialData }: Params) => {
  return useQuery<CareRecordListResponseDto>({
    queryKey: ["careRecords", startDate.toISOString(), endDate.toISOString()],
    initialData, // 初期データ（今週の場合のみサーバーで取得）
    staleTime: initialData ? 1000 * 60 : 0, // 初期データが渡されている場合、1分間自動通信を防止
    queryFn: async () => {
      const res = await fetch(
        `/api/care-records?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`,
      );
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error ?? "育児記録の取得に失敗しました");
      }
      return res.json() as Promise<CareRecordListResponseDto>;
    },
  });
};
