"use server";

import { revalidatePath } from "next/cache";

import { SharingGroupManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  name: string;
  userIds: number[];
};

type ActionResult = { success: true } | { success: false; error: string };

export const createGroupAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、SharingGroupManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new SharingGroupManagementApi(configuration);

    await apiClient.createSharingGroup({
      xRequestedWith: "XMLHttpRequest",
      sharingGroupData: {
        name: input.name,
        userIds: input.userIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true };
  } catch (error) {
    console.error("createGroupAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "グループ作成に失敗しました" };
  }
};
