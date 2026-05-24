import "server-only";

import { type MediaCommentResponseDto, MediaCommentManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getMediaComments = async (mediaId: number): Promise<MediaCommentResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new MediaCommentManagementApi(configuration);
  return apiClient.getMediaComments({
    xRequestedWith: "XMLHttpRequest",
    mediaId,
  });
};
