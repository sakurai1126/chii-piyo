"use server";

import { revalidatePath } from "next/cache";

import { MediaCommentManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  commentId: number;
};

export const deleteCommentAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaCommentManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaCommentManagementApi(configuration);

    await apiClient.deleteMediaComment({
      xRequestedWith: "XMLHttpRequest",
      id: input.commentId,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/media", "layout");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "コメントの削除に失敗しました");
  }
};
