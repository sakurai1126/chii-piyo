"use server";

import { revalidatePath } from "next/cache";

import { SharingGroupManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  name: string;
  userIds: number[];
};

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
    revalidatePath("/settings");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "グループ作成に失敗しました");
  }
};
