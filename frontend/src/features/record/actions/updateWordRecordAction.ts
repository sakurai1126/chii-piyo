"use server";

import { revalidatePath } from "next/cache";

import { WordRecordManagementApi, WordRecordRequestDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  id: number;
  title: string;
  recordedDate: Date;
  comment: string;
  mediaIds: number[];
};

export const updateWordRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、WordRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new WordRecordManagementApi(configuration);

    const requestDto: WordRecordRequestDto = {
      title: input.title,
      recordedDate: input.recordedDate,
      comment: input.comment,
      mediaIds: input.mediaIds,
    };

    await apiClient.updateWordRecord({
      xRequestedWith: "XMLHttpRequest",
      id: input.id,
      wordRecordData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/word-records");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "ことばの記録の更新に失敗しました");
  }
};
