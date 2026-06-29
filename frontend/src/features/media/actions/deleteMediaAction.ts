"use server";

import { MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
};

export const deleteMediaAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    await apiClient.deleteMedia({
      xRequestedWith: "XMLHttpRequest",
      id: input.mediaId,
    });

    return { success: true };
  } catch (error) {
    return handleActionError(error, "メディアの削除に失敗しました");
  }
};
