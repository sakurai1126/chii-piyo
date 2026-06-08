"use server";

import { revalidatePath } from "next/cache";

import { MediaCommentManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  commentId: number;
};

type ActionResult = { success: true } | { success: false; error: string };

export const deleteCommentAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaCommentManagementApi(configuration);

    await apiClient.deleteMediaComment({
      xRequestedWith: "XMLHttpRequest",
      id: input.commentId,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    console.error("deleteCommentAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "コメントの削除に失敗しました" };
  }
};
