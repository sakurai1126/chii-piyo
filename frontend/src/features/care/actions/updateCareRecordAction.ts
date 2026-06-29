"use server";

import { revalidatePath } from "next/cache";

import {
  CareRecordManagementApi,
  CareRecordRequestDtoRecordTypeEnum,
  DiaperDetailDto,
  HealthDetailDto,
  MealDetailDto,
  MilkDetailDto,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  id: number;
  recordType: CareRecordRequestDtoRecordTypeEnum;
  recordedAt: Date;
  mealDetail?: MealDetailDto;
  milkDetail?: MilkDetailDto;
  diaperDetail?: DiaperDetailDto;
  healthDetail?: HealthDetailDto;
};

export const updateCareRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、CareRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new CareRecordManagementApi(configuration);

    await apiClient.updateCareRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
      careRecordData: {
        recordType: input.recordType,
        recordedAt: input.recordedAt,
        mealDetail: input.mealDetail ?? null,
        milkDetail: input.milkDetail ?? null,
        diaperDetail: input.diaperDetail ?? null,
        healthDetail: input.healthDetail ?? null,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/care");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "更新に失敗しました");
  }
};
