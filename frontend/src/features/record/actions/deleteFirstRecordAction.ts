"use server";

import { revalidatePath } from "next/cache";

import { FirstRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  id: number;
};

export const deleteFirstRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、FirstRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new FirstRecordManagementApi(configuration);

    await apiClient.deleteFirstRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/first-records");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "記録の削除に失敗しました");
  }
};
