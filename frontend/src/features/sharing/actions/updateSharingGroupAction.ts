"use server";

import { revalidatePath } from "next/cache";

import { SharingGroupManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  groupId: number;
  name?: string;
  userIds?: number[];
};

export const updateSharingGroupAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、SharingGroupManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new SharingGroupManagementApi(configuration);

    await apiClient.updateSharingGroup({
      xRequestedWith: "XMLHttpRequest",
      id: input.groupId,
      sharingGroupUpdateData: {
        name: input.name,
        userIds: input.userIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/settings");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "共有グループの更新に失敗しました");
  }
};
