import "server-only";

import { AlbumResponseDto, AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

type Props = {
  albumId: number;
};

export const getAlbum = async ({ albumId }: Props): Promise<AlbumResponseDto> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new AlbumManagementApi(configuration);
  return apiClient.getAlbum({
    xRequestedWith: "XMLHttpRequest",
    id: albumId,
  });
};
