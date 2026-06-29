import { NextRequest, NextResponse } from "next/server";

/**
 * リクエストから startDate と endDate を取得して検証する共通処理
 */
export const validateDateRangeParams = (request: NextRequest) => {
  const params = request.nextUrl.searchParams;
  const startDateParam = params.get("startDate");
  const endDateParam = params.get("endDate");

  if (!startDateParam || !endDateParam) {
    return {
      errorResponse: NextResponse.json(
        { error: "startDate と endDate は必須です" },
        { status: 400 },
      ),
    };
  }

  const startDate = new Date(startDateParam);
  const endDate = new Date(endDateParam);

  if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
    return {
      errorResponse: NextResponse.json(
        { error: "startDate または endDate の形式が不正です" },
        { status: 400 },
      ),
    };
  }

  return { startDate, endDate };
};

/**
 * API Route における共通エラーハンドリング
 */
export const handleApiError = (error: unknown, defaultMessage: string) => {
  if (error instanceof Error && error.message === "UNAUTHORIZED") {
    return NextResponse.json({ error: "認証が必要です" }, { status: 401 });
  }
  console.error(defaultMessage, error);
  return NextResponse.json({ error: defaultMessage }, { status: 500 });
};
