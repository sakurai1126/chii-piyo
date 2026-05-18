import "server-only";

import {
  GetMediaListMediaKindEnum,
  type MediaListResponseDto,
  MediaManagementApi,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export type GetMediaListParams = {
  offset?: number;
  limit?: number;
  mediaKind?: GetMediaListMediaKindEnum;
  albumId?: number;
  tagId?: number;
  sharingGroupId?: number;
  startDate?: Date;
  endDate?: Date;
};

export const getMediaList = async (
  params: GetMediaListParams = {},
): Promise<MediaListResponseDto> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new MediaManagementApi(configuration);
  return apiClient.getMediaList({
    xRequestedWith: "XMLHttpRequest",
    ...params,
  });
};
