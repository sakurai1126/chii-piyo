"use server";

import { revalidatePath } from "next/cache";

import { FavoriteManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
};

export const removeFavoriteAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、FavoriteManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new FavoriteManagementApi(configuration);

    await apiClient.removeFavorite({
      xRequestedWith: "XMLHttpRequest",
      mediaId: input.mediaId,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/");
    revalidatePath("/media");
    revalidatePath(`/media/${input.mediaId}`);
    revalidatePath("/favorites");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "お気に入りの削除に失敗しました");
  }
};
