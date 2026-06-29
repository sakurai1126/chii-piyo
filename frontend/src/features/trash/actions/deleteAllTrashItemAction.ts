"use server";

import { revalidatePath } from "next/cache";

import { TrashManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

export const deleteAllTrashItemAction = async (): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TrashManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TrashManagementApi(configuration);

    await apiClient.emptyTrash({
      xRequestedWith: "XMLHttpRequest",
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/trash");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "メディアの削除に失敗しました");
  }
};
