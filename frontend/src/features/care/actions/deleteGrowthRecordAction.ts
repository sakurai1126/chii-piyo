"use server";

import { revalidatePath } from "next/cache";

import { GrowthRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  id: number;
};

export const deleteGrowthRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、GrowthRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new GrowthRecordManagementApi(configuration);

    await apiClient.deleteGrowthRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/care");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "記録の削除に失敗しました");
  }
};
