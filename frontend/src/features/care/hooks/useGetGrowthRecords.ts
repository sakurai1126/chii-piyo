"use client";

import { useQuery } from "@tanstack/react-query";

import { GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateBasic } from "@/utils/date";
import { fetchApi } from "@/utils/fetcher";

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
      const startStr = formatJapaneseDateBasic(startDate);
      const endStr = formatJapaneseDateBasic(endDate);

      // 共通化した fetchApi関数 を利用してAPIを呼び出す
      return fetchApi<GrowthRecordResponseDto[]>(
        `/api/growth-records?startDate=${startStr}&endDate=${endStr}`,
        "成長記録の取得に失敗しました",
      );
    },
  });
};
