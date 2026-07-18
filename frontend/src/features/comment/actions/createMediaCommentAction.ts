"use server";

import {
  MediaCommentManagementApi,
  MediaCommentRequestDto,
  MediaCommentResponseDto,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
  content: string;
};

export const createMediaCommentAction = async (
  input: Input,
): Promise<ActionResult<MediaCommentResponseDto>> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、MediaCommentManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new MediaCommentManagementApi(configuration);

    const requestDto: MediaCommentRequestDto = {
      content: input.content,
    };

    const response = await apiClient.createMediaComment({
      xRequestedWith: "XMLHttpRequest",
      mediaId: input.mediaId,
      mediaCommentData: requestDto,
    });

    return { success: true, data: response };
  } catch (error) {
    return handleActionError(error, "コメント作成に失敗しました");
  }
};
