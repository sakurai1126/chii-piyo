"use server";

import { MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
};

type ActionResult = { success: true } | { success: false; error: string };

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
    console.error("deleteMediaAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディアの削除に失敗しました" };
  }
};
