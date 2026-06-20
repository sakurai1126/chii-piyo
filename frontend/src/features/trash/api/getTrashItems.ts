import "server-only";

import { TrashItemListResponseDto, TrashManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

type Params = {
  offset?: number;
  limit?: number;
};

export const getTrashItems = async ({
  offset,
  limit,
}: Params): Promise<TrashItemListResponseDto> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new TrashManagementApi(configuration);
  return apiClient.getTrashItems({
    xRequestedWith: "XMLHttpRequest",
    offset,
    limit,
  });
};
