"use client";

import { useCallback, useEffect, useState } from "react";

import { type AlbumResponseDto } from "@/lib/api-client/gen";

import { getAlbumsAction } from "../actions/getAlbumsAction";
import { UseAlbumsResult } from "../types";

export const useAlbums = (): UseAlbumsResult => {
  const [albums, setAlbums] = useState<AlbumResponseDto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 初回マウント時の取得処理
  useEffect(() => {
    const controller = new AbortController();

    (async () => {
      // 中断されていたら処理しない
      if (controller.signal.aborted) return;

      const result = await getAlbumsAction();

      // 成功時はアルバムをセット、失敗時はエラーをセット
      if (result.success) {
        setAlbums(result.data);
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

    const result = await getAlbumsAction();

    if (result.success) {
      setAlbums(result.data);
    } else {
      setError(result.error);
    }
    setIsLoading(false);
  }, []);

  return {
    albums,
    isLoading,
    error,
    refetch,
  };
};
