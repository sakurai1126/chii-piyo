import "server-only";

import { WordRecordManagementApi, WordRecordResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

export const getWordRecords = async (): Promise<WordRecordResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new WordRecordManagementApi(configuration);
  return apiClient.getWordRecords({
    xRequestedWith: "XMLHttpRequest",
  });
};
