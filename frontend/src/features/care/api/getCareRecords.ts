import "server-only";

import { CareRecordListResponseDto, CareRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { formatJapaneseDateBasic } from "@/utils/date";

type Props = {
  startDate: Date;
  endDate: Date;
};

export const getCareRecords = async ({
  startDate,
  endDate,
}: Props): Promise<CareRecordListResponseDto> => {
  const configuration = await createAuthorizedConfig();
  const apiClient = new CareRecordManagementApi(configuration);
  return apiClient.getCareRecords({
    xRequestedWith: "XMLHttpRequest",
    startDate: new Date(formatJapaneseDateBasic(startDate)),
    endDate: new Date(formatJapaneseDateBasic(endDate)),
  });
};
