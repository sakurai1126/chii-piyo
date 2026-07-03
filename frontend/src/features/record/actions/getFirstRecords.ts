import "server-only";

import { FirstRecordManagementApi, FirstRecordResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getFirstRecords = async (): Promise<FirstRecordResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new FirstRecordManagementApi(configuration);
  return apiClient.getFirstRecords({
    xRequestedWith: "XMLHttpRequest",
  });
};
