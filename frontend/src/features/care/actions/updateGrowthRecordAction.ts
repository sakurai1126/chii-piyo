"use server";

import { revalidatePath } from "next/cache";

import { GrowthRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  id: number;
  measurementDate: Date;
  height: number | undefined;
  weight: number | undefined;
  note: string;
};

export const updateGrowthRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、GrowthRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new GrowthRecordManagementApi(configuration);

    await apiClient.updateGrowthRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
      growthRecordData: {
        measurementDate: input.measurementDate,
        height: input.height,
        weight: input.weight,
        note: input.note,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/care");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "更新に失敗しました");
  }
};
