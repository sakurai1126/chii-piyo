"use server";

import { revalidatePath } from "next/cache";

import { MediaCommentManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
  content: string;
};

type ActionResult = { success: true } | { success: false; error: string };

export const createCommentAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaCommentManagementApi(configuration);

    await apiClient.createMediaComment({
      xRequestedWith: "XMLHttpRequest",
      mediaId: input.mediaId,
      mediaCommentData: {
        content: input.content,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    console.error("createCommentAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディアのコメント追加に失敗しました" };
  }
};
