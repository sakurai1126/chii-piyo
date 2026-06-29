import { NextResponse } from "next/server";

import { SharingGroupManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleApiError } from "@/utils/api";

export const GET = async () => {
  try {
    const configuration = await createAuthorizedConfig();
    const apiClient = new SharingGroupManagementApi(configuration);

    const data = await apiClient.getSharingGroups({
      xRequestedWith: "XMLHttpRequest",
    });

    return NextResponse.json(data);
  } catch (error) {
    return handleApiError(error, "共有グループの取得に失敗しました");
  }
};
