"use server";

import { revalidatePath } from "next/cache";

import { CareRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  id: number;
};

type ActionResult = { success: true } | { success: false; error: string };

export const deleteCareRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、CareRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new CareRecordManagementApi(configuration);

    await apiClient.deleteCareRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/care");

    return { success: true };
  } catch (error) {
    console.error("deleteCareRecordAction", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "記録の削除に失敗しました" };
  }
};
