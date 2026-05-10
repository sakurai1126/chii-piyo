"use server";

// クライアントに返す結果型
import { TagManagementApi, TagRequestDto, TagResponseDto } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

// 例外をクライアントに直接出さず、成功/失敗を判別可能な形にする
export type ActionResult =
  | { success: true; data: TagResponseDto }
  | { success: false; error: string };

// クライアントから受け取る入力型
type Input = {
  name: string;
};

export const createTagAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、TagManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new TagManagementApi(configuration);

    const requestDto: TagRequestDto = {
      name: input.name,
    };

    const response = await apiClient.createTag({
      xRequestedWith: "XMLHttpRequest",
      tagData: requestDto,
    });

    return { success: true, data: response };
  } catch (error) {
    console.error("createTagAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "タグ登録に失敗しました" };
  }
};
