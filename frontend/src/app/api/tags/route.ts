import { NextResponse } from "next/server";

import { TagManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const GET = async () => {
  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    const data = await apiClient.getTags({
      xRequestedWith: "XMLHttpRequest",
    });

    return NextResponse.json(data);
  } catch (error) {
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return NextResponse.json({ error: "認証が必要です" }, { status: 401 });
    }
    console.error("GET /api/tags失敗", error);
    return NextResponse.json({ error: "タグの取得に失敗しました" }, { status: 500 });
  }
};
