import { NextRequest, NextResponse } from "next/server";

import { CareRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleApiError, validateDateRangeParams } from "@/utils/api";

export const GET = async (request: NextRequest) => {
  // 日時範囲パラメータのバリデーション処理
  const { startDate, endDate, errorResponse } = validateDateRangeParams(request);
  if (errorResponse) return errorResponse;

  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new CareRecordManagementApi(configuration);

    const data = await apiClient.getCareRecords({
      xRequestedWith: "XMLHttpRequest",
      startDate,
      endDate,
    });

    return NextResponse.json(data);
  } catch (error) {
    return handleApiError(error, "育児記録の取得に失敗しました");
  }
};
