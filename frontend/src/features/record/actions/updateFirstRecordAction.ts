"use server";

import { revalidatePath } from "next/cache";

import { FirstRecordManagementApi, FirstRecordRequestDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";
import { dateOnlyToUtcNoon } from "@/utils/date";

// クライアントから受け取る入力型
type Input = {
  id: number;
  title: string;
  recordedDate: string;
  comment: string;
  mediaIds: number[];
};

export const updateFirstRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、FirstRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new FirstRecordManagementApi(configuration);

    const requestDto: FirstRecordRequestDto = {
      title: input.title,
      recordedDate: dateOnlyToUtcNoon(input.recordedDate),
      comment: input.comment,
      mediaIds: input.mediaIds,
    };

    await apiClient.updateFirstRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
      firstRecordData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/first-records");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "はじめて記録の更新に失敗しました");
  }
};
