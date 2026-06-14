import "server-only";

import { TagManagementApi, TagResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getTags = async (): Promise<TagResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new TagManagementApi(configuration);
  return apiClient.getTags({
    xRequestedWith: "XMLHttpRequest",
  });
};
