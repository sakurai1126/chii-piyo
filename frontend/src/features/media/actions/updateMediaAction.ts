"use server";

import { revalidatePath } from "next/cache";

import { MediaManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
  albumId?: number | null;
  sharingGroupId?: number | null;
};

export const updateMediaAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaManagementApi(configuration);

    await apiClient.updateMedia({
      xRequestedWith: "XMLHttpRequest",
      id: input.mediaId,
      mediaUpdateData: {
        albumId: input.albumId,
        sharingGroupId: input.sharingGroupId,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath(`/media/${input.mediaId}`);

    return { success: true };
  } catch (error) {
    return handleActionError(error, "メディアの更新に失敗しました");
  }
};
