"use server";

import { revalidatePath } from "next/cache";

import { AlbumManagementApi, AlbumRequestDto, AlbumResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult =
  | { success: true; data: AlbumResponseDto }
  | { success: false; error: string };

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

    const response = await apiClient.createAlbum({
      xRequestedWith: "XMLHttpRequest",
      albumData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/albums");

    return { success: true, data: response };
  } catch (error) {
    console.error("createAlbumAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "アルバム作成に失敗しました" };
  }
};
