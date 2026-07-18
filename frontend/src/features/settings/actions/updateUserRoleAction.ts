"use server";

import { revalidatePath } from "next/cache";

import { getCurrentUser } from "@/features/auth/server";
import { UserManagementApi, UserRoleUpdateRequestDtoRoleEnum } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  userId: number;
  newRole: UserRoleUpdateRequestDtoRoleEnum;
};

export const updateUserRoleAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 自己の変更ではないかをチェック
    const currentUser = await getCurrentUser();
    if (currentUser.id === input.userId) {
      return { success: false, error: "自分自身の権限は変更できません" };
    }

    // 認証トークンを含むAPIクライアントの設定を生成し、MediaManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new UserManagementApi(configuration);

    await apiClient.updateRole({
      xRequestedWith: "XMLHttpRequest",
      id: input.userId,
      userUpdateRoleData: {
        role: input.newRole,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/settings");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "権限の変更に失敗しました");
  }
};
