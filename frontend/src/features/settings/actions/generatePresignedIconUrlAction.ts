"use server";

import { UserManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";

type ActionResult =
  | { success: true; presignedUrl: string; s3key: string }
  | { success: false; error: string };

type Input = {
  filename: string;
  contentType: string;
};

/**
 * アイコンの情報を送信し署名付きアップロード用URLを受け取るサーバーアクション
 *
 * @param input
 * 送信するアイコン情報
 *
 * @returns
 * 成功時：成功フラグ + 署名付きURL + ユーザーアイコンのキー + S3キー
 * 失敗時：失敗フラグ + エラーメッセージ
 */
export const generatePresignedIconUrlAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、UserManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new UserManagementApi(configuration);

    // APIを呼び出してメタデータ登録と署名付きURLの取得を行う
    const response = await apiClient.generateIconPresignedUrl({
      xRequestedWith: "XMLHttpRequest",
      userGenerateIconData: {
        filename: input.filename,
        contentType: input.contentType,
      },
    });
    return {
      success: true,
      presignedUrl: response.presignedUrl,
      s3key: response.s3key,
    };
  } catch (error) {
    console.error("generatePresignedIconUrlAction失敗", error);
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return { success: false, error: "認証が必要です" };
    }
    return { success: false, error: "プロフィール更新に失敗しました" };
  }
};
