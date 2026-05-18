import { NextResponse } from "next/server";

import { AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const GET = async () => {
  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new AlbumManagementApi(configuration);

    const data = await apiClient.getAlbums({
      xRequestedWith: "XMLHttpRequest",
    });

    return NextResponse.json(data);
  } catch (error) {
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return NextResponse.json({ error: "認証が必要です" }, { status: 401 });
    }
    console.error("GET /api/albums失敗", error);
    return NextResponse.json({ error: "アルバムの取得に失敗しました" }, { status: 500 });
  }
};
