"use server";

import { revalidatePath } from "next/cache";

import { MediaTagsUpdateRequestDto, TagManagementApi, TagResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
  tagIds: number[];
};

export const updateMediaTagsAction = async (
  input: Input,
): Promise<ActionResult<TagResponseDto[]>> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TagManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    const requestDto: MediaTagsUpdateRequestDto = {
      tagIds: input.tagIds,
    };

    const response = await apiClient.updateMediaTags({
      xRequestedWith: "XMLHttpRequest",
      mediaId: input.mediaId,
      mediaTagsData: requestDto,
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/media", "layout");

    return { success: true, data: response };
  } catch (error) {
    return handleActionError(error, "タグ登録に失敗しました");
  }
};
