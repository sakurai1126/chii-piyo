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

// クライアントに返す結果型
// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult = { success: true } | { success: false; error: string };

// クライアントから受け取る入力型
type Input = {
  recordType: CareRecordRequestDtoRecordTypeEnum;
  recordedAt: Date;
  mealDetail?: MealDetailDto;
  milkDetail?: MilkDetailDto;
  diaperDetail?: DiaperDetailDto;
  healthDetail?: HealthDetailDto;
};

export const createCareRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、AlbumManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new CareRecordManagementApi(configuration);

    await apiClient.createCareRecord({
      xRequestedWith: "XMLHttpRequest",
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
    console.error("createCareRecordAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "記録に失敗しました" };
  }
};
