"use server";

import { revalidatePath } from "next/cache";

import { TagManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  tagId: number;
};

export const deleteTagAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    await apiClient.deleteTag({
      xRequestedWith: "XMLHttpRequest",
      id: input.tagId,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/settings");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "タグの削除に失敗しました");
  }
};
