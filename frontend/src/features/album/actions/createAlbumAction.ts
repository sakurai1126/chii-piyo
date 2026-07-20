"use server";

import { revalidatePath } from "next/cache";

import { AlbumManagementApi, AlbumRequestDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  title: string;
};

export const createAlbumAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、AlbumManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new AlbumManagementApi(configuration);

    const requestDto: AlbumRequestDto = {
      title: input.title,
    };

    await apiClient.createAlbum({
      xRequestedWith: "XMLHttpRequest",
      albumData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/albums");
    revalidatePath("/settings");
    revalidatePath("/upload");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "アルバム作成に失敗しました");
  }
};
