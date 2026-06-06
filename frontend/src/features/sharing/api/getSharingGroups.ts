import "server-only";

import { SharingGroupManagementApi, SharingGroupResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getSharingGroups = async (): Promise<SharingGroupResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new SharingGroupManagementApi(configuration);
  return apiClient.getSharingGroups({
    xRequestedWith: "XMLHttpRequest",
  });
};
