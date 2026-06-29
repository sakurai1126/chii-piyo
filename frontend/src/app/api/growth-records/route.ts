import { NextRequest, NextResponse } from "next/server";

import { GrowthRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleApiError, validateDateRangeParams } from "@/utils/api";

export const GET = async (request: NextRequest) => {
  // 日時範囲パラメータのバリデーション処理
  const { startDate, endDate, errorResponse } = validateDateRangeParams(request);
  if (errorResponse) return errorResponse;

  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new GrowthRecordManagementApi(configuration);

    const data = await apiClient.getGrowthRecords({
      xRequestedWith: "XMLHttpRequest",
      startDate,
      endDate,
    });

    return NextResponse.json(data);
  } catch (error) {
    return handleApiError(error, "成長記録の取得に失敗しました");
  }
};
