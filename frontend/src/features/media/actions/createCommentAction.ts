"use server";

import { revalidatePath } from "next/cache";

import { MediaCommentManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
  content: string;
};

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
    revalidatePath(`/media/${input.mediaId}`);

    return { success: true };
  } catch (error) {
    return handleActionError(error, "メディアのコメント追加に失敗しました");
  }
};
