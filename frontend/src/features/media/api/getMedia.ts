import "server-only";

import { type MediaResponseDto, MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getMedia = async (id: number): Promise<MediaResponseDto> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new MediaManagementApi(configuration);
  return apiClient.getMedia({
    xRequestedWith: "XMLHttpRequest",
    id,
  });
};
