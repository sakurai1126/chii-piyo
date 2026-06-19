import "server-only";

import { AlbumResponseDto, AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getAlbums = async (): Promise<AlbumResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new AlbumManagementApi(configuration);
  return apiClient.getAlbums({
    xRequestedWith: "XMLHttpRequest",
  });
};
