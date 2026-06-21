"use server";

import { revalidatePath } from "next/cache";

import { TrashManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult = { success: true } | { success: false; error: string };

// クライアントから受け取る入力型
type Input = {
  trashItemIds: number[];
};

export const deleteTrashItemsAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TrashManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TrashManagementApi(configuration);

    await apiClient.deleteTrashItems({
      xRequestedWith: "XMLHttpRequest",
      trashItemIds: input.trashItemIds,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/trash");

    return { success: true };
  } catch (error) {
    console.error("deleteTrashItemsAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディアの削除に失敗しました" };
  }
};
