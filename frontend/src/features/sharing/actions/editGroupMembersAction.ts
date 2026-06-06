"use server";

import { revalidatePath } from "next/cache";

import { SharingGroupManagementApi, SharingGroupResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントから受け取る入力型
type Input = {
  groupId: number;
  userIds: number[];
};

type ActionResult =
  | { success: true; sharingGroup: SharingGroupResponseDto }
  | { success: false; error: string };

export const editGroupMembersAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、UserManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new SharingGroupManagementApi(configuration);

    const response = await apiClient.editSharingGroupMember({
      xRequestedWith: "XMLHttpRequest",
      id: input.groupId,
      sharingGroupMemberData: {
        userIds: input.userIds,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/", "layout");

    return { success: true, sharingGroup: response };
  } catch (error) {
    console.error("editGroupMembersAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "メンバー更新に失敗しました" };
  }
};
