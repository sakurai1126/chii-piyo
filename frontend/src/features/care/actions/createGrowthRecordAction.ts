"use server";

import { revalidatePath } from "next/cache";

import { GrowthRecordManagementApi } from "@/lib/api-client/gen";
import { createAuthorizedConfig } from "@/lib/api-client/server";
import { handleActionError, ActionResult } from "@/utils/action";

// クライアントから受け取る入力型
type Input = {
  measurementDate: Date;
  height?: number;
  weight?: number;
  note: string;
};

export const createGrowthRecordAction = async (input: Input): Promise<ActionResult> => {
  try {
    // 認証トークンを含むAPIクライアントの設定を生成し、GrowthRecordManagementApiのインスタンスを作成
    const configuration = await createAuthorizedConfig();
    const apiClient = new GrowthRecordManagementApi(configuration);

    // measurementDateは時刻なしのためDateそのままだと保存時にずれが生じるため送信前にタイムゾーンを日本時間に補正
    const inputMeasurementDate = new Date(input.measurementDate);
    const offsetDate = new Date(
      inputMeasurementDate.getTime() - inputMeasurementDate.getTimezoneOffset() * 60000,
    );

    await apiClient.createGrowthRecord({
      xRequestedWith: "XMLHttpRequest",
      growthRecordData: {
        measurementDate: offsetDate,
        height: input.height,
        weight: input.weight,
        note: input.note,
      },
    });

    // キャッシュを破棄し、サーバーコンポーネントを再レンダリング
    revalidatePath("/care");

    return { success: true };
  } catch (error) {
    return handleActionError(error, "記録に失敗しました");
  }
};
