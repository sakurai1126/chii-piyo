"use server";

import { revalidatePath } from "next/cache";

import { TagManagementApi, TagRequestDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  name: string;
};

export const createTagAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TagManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    const requestDto: TagRequestDto = {
      name: input.name,
    };

    await apiClient.createTag({
      xRequestedWith: "XMLHttpRequest",
      tagData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/settings");
    revalidatePath("/upload");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "タグ登録に失敗しました");
  }
};
