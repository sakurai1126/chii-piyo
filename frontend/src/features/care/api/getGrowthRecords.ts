import "server-only";

import { GrowthRecordManagementApi, GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

type Props = {
  startDate: Date;
  endDate: Date;
};

export const getGrowthRecords = async ({
  startDate,
  endDate,
}: Props): Promise<GrowthRecordResponseDto[]> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new GrowthRecordManagementApi(configuration);
  return apiClient.getGrowthRecords({
    xRequestedWith: "XMLHttpRequest",
    startDate,
    endDate,
  });
};
