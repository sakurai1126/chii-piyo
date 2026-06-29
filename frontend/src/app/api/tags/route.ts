import { NextResponse } from "next/server";

import { TagManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleApiError } from "@/utils/api";

export const GET = async () => {
  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    const data = await apiClient.getTags({
      xRequestedWith: "XMLHttpRequest",
    });

    return NextResponse.json(data);
  } catch (error) {
    return handleApiError(error, "タグの取得に失敗しました");
  }
};
