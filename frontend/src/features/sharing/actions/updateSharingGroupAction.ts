"use server";

import { revalidatePath } from "next/cache";

import { SharingGroupManagementApi, SharingGroupResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  groupId: number;
  name?: string;
  userIds?: number[];
};

type ActionResult =
  | { success: true; sharingGroup: SharingGroupResponseDto }
  | { success: false; error: string };

export const updateSharingGroupAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、SharingGroupManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new SharingGroupManagementApi(configuration);

    const response = await apiClient.updateSharingGroup({
      xRequestedWith: "XMLHttpRequest",
      id: input.groupId,
      sharingGroupUpdateData: {
        name: input.name,
        userIds: input.userIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true, sharingGroup: response };
  } catch (error) {
    console.error("updateSharingGroupAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "共有グループ更新に失敗しました" };
  }
};
