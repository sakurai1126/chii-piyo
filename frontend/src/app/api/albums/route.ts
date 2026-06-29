import { NextResponse } from "next/server";

import { AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleApiError } from "@/utils/api";

export const GET = async () => {
  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new AlbumManagementApi(configuration);

    const data = await apiClient.getAlbums({
      xRequestedWith: "XMLHttpRequest",
    });

    return NextResponse.json(data);
  } catch (error) {
    return handleApiError(error, "アルバムの取得に失敗しました");
  }
};
