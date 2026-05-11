"use server";

import {
  MediaCommentManagementApi,
  MediaCommentRequestDto,
  MediaCommentResponseDto,
} from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// クライアントに返す結果型
// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult =
  | { success: true; data: MediaCommentResponseDto }
  | { success: false; error: string };

// クライアントから受け取る入力型
type Input = {
  mediaId: number;
  content: string;
};

export const createMediaCommentAction = async (input: Input): Promise<ActionResult> => {
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
    console.error("createMediaCommentAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "コメント作成に失敗しました" };
  }
};
