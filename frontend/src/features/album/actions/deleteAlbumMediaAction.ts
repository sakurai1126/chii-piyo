"use server";

import { revalidatePath } from "next/cache";

import { AlbumManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  albumId: number;
  mediaIds: number[];
};

type ActionResult = { success: true } | { success: false; error: string };

export const deleteAlbumMediaAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、AlbumManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new AlbumManagementApi(configuration);

    await apiClient.deleteAlbumMedia({
      xRequestedWith: "XMLHttpRequest",
      id: input.albumId,
      mediaIds: input.mediaIds,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath(`/albums/${input.albumId}`);

    return { success: true };
  } catch (error) {
    console.error("deleteAlbumMediaAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "アルバムからのメディア削除に失敗しました" };
  }
};
