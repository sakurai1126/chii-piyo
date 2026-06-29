"use server";

import { revalidatePath } from "next/cache";

import { AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  albumId: number;
  mediaIds: number[];
};

export const addAlbumMediaAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、AlbumManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new AlbumManagementApi(configuration);

    await apiClient.addAlbumMedia({
      xRequestedWith: "XMLHttpRequest",
      id: input.albumId,
      albumMediaData: {
        mediaIds: input.mediaIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath(`/albums/${input.albumId}`);

    return { success: true };
  } catch (error) {
    return handleActionError(error, "アルバムへのメディア追加に失敗しました");
  }
};
