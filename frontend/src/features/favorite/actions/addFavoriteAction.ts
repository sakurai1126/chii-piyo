"use server";

import { revalidatePath } from "next/cache";

import { FavoriteManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
};

type ActionResult = { success: true } | { success: false; error: string };

export const addFavoriteAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、FavoriteManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new FavoriteManagementApi(configuration);

    await apiClient.addFavorite({
      xRequestedWith: "XMLHttpRequest",
      mediaId: input.mediaId,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    console.error("addFavoriteAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "お気に入りの追加に失敗しました" };
  }
};
