"use server";

import { revalidatePath } from "next/cache";

import { MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaIds: number[];
  sharingGroupId?: number;
  tagIds?: number[];
};

export const updateMediaBatchAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    await apiClient.updateMediaBatch({
      xRequestedWith: "XMLHttpRequest",
      mediaBatchUpdateData: {
        mediaIds: input.mediaIds,
        sharingGroupId: input.sharingGroupId,
        tagIds: input.tagIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "メディアの更新に失敗しました");
  }
};
