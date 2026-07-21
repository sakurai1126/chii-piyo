"use server";

import { revalidatePath } from "next/cache";

import { GrowthRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";
import { dateOnlyToUtcNoon } from "@/utils/date";

// クライアントから受け取る入力型
type Input = {
  measurementDate: string;
  height?: number;
  weight?: number;
  note: string;
};

export const createGrowthRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、GrowthRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new GrowthRecordManagementApi(configuration);

    await apiClient.createGrowthRecord({
      xRequestedWith: "XMLHttpRequest",
      growthRecordData: {
        measurementDate: dateOnlyToUtcNoon(input.measurementDate),
        height: input.height,
        weight: input.weight,
        note: input.note,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/");
    revalidatePath("/care");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "記録に失敗しました");
  }
};
