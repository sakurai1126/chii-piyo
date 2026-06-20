"use server";

import { revalidatePath } from "next/cache";

import { MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  mediaIds: number[];
};

type ActionResult = { success: true } | { success: false; error: string };

export const deleteMultipleMediaAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    await apiClient.deleteMultipleMedia({
      xRequestedWith: "XMLHttpRequest",
      mediaIds: input.mediaIds,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/media");

    return { success: true };
  } catch (error) {
    console.error("deleteMultipleMediaAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディアの削除に失敗しました" };
  }
};
