import { NextRequest, NextResponse } from "next/server";

import { GrowthRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const GET = async (request: NextRequest) => {
  const params = request.nextUrl.searchParams;

  const startDateParam = params.get("startDate");
  const endDateParam = params.get("endDate");
  if (!startDateParam || !endDateParam) {
    return NextResponse.json({ error: "startDate と endDate は必須です" }, { status: 400 });
  }

  const startDate = new Date(startDateParam);
  const endDate = new Date(endDateParam);
  if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
    return NextResponse.json(
      { error: "startDate または endDate の形式が不正です" },
      { status: 400 },
    );
  }

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
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return NextResponse.json({ error: "認証が必要です" }, { status: 401 });
    }
    console.error("GET /api/growth-records失敗", error);
    return NextResponse.json({ error: "成長記録の取得に失敗しました" }, { status: 500 });
  }
};
