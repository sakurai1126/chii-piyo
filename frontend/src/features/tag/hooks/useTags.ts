"use client";

import { useCallback, useEffect, useState } from "react";

import { type TagResponseDto } from "@/lib/api-client/gen";

import { getTagsAction } from "../actions/getTagsAction";
import { UseTagsResult } from "../types";

export const useTags = (): UseTagsResult => {
  const [tags, setTags] = useState<TagResponseDto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 初回マウント時の取得処理
  useEffect(() => {
    const controller = new AbortController();

    (async () => {
      // 中断されていたら処理しない
      if (controller.signal.aborted) return;

      const result = await getTagsAction();

      // 成功時はタグをセット、失敗時はエラーをセット
      if (result.success) {
        setTags(result.data);
      } else {
        setError(result.error);
      }

      // ローディング終了
      setIsLoading(false);
    })();

    // アンマウント時は取得処理を中断
    return () => controller.abort();
  }, []);

  // 手動再試行時の取得処理
  const refetch = useCallback(async () => {
    // 再試行開始時はローディング状態にしてエラーをリセット
    setIsLoading(true);
    setError(null);

    const result = await getTagsAction();

    if (result.success) {
      setTags(result.data);
    } else {
      setError(result.error);
    }
    setIsLoading(false);
  }, []);

  return {
    tags,
    isLoading,
    error,
    refetch,
  };
};
