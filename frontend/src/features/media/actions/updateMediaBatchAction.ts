"use server";

import { revalidatePath } from "next/cache";

import { MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  mediaIds: number[];
  albumId?: number;
  sharingGroupId?: number;
  tagIds?: number[];
};

type ActionResult = { success: true } | { success: false; error: string };

export const updateMediaBatchAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    await apiClient.updateMediaBatch({
      xRequestedWith: "XMLHttpRequest",
      mediaBatchUpdateData: {
        mediaIds: input.mediaIds,
        albumId: input.albumId,
        sharingGroupId: input.sharingGroupId,
        tagIds: input.tagIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    console.error("updateMediaBatchAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メディアの更新に失敗しました" };
  }
};
