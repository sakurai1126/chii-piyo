"use server";

import { revalidatePath } from "next/cache";

import { TagManagementApi, TagRequestDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult = { success: true } | { success: false; error: string };

// クライアントから受け取る入力型
type Input = {
  tagId: number;
  name: string;
};

export const updateTagAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TagManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    const requestDto: TagRequestDto = {
      name: input.name,
    };

    await apiClient.updateTag({
      xRequestedWith: "XMLHttpRequest",
      id: input.tagId,
      tagUpdateData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    console.error("updateTagAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "タグ更新に失敗しました" };
  }
};
