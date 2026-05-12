"use client";

import { useCallback, useEffect, useRef, useState } from "react";

import { ItemState, UploadMedia, UploadMetadata } from "../types";

/**
 * 画像ファイルのアップロードに関連する状態管理を提供するフック
 *
 * @returns
 * - items: アップロード対象の画像アイテムの配列
 * - setFileAndUrl: ファイルを受け取って初期値、プレビューURL、縦横サイズを取得しセットする関数
 * - removeFile: 指定したインデックスのアイテムを削除し、URLを解放する関数
 * - removeAllFiles: すべてのアイテムを削除し、URLを解放する関数
 * - updateItem: 指定したアイテムIDの状態を更新する関数
 */
export const useUploadMediaState = () => {
  const [items, setItems] = useState<UploadMedia[]>([]);

  // クリーンアップ時にitemsの最新値を参照するためのref
  // 続くアンマウント時の処理にてアップデート関数のsetItems経由での読み取りを避けるため使用
  const itemsRef = useRef(items);
  useEffect(() => {
    itemsRef.current = items;
  }, [items]);

  // useCallbackで関数をメモ化して、子コンポーネントへの不要な再レンダリングを防止

  /**
   * 画像ファイルとプレビューURL、縦横サイズをセットする
   */
  const setImageAndUrl = useCallback(async (files: File[]) => {
    // 複数ファイルを読み込むためにPromise.allでloadImageFileを並列実行する
    try {
      const newItems = await Promise.all(files.map(loadImageFile));
      setItems((prev) => [...prev, ...newItems]);
    } catch {
      console.error("画像の読み込みに失敗しました");
    }
  }, []);

  /**
   * 動画ファイルとプレビューURL、縦横サイズをセットする
   */
  const setVideoAndUrl = useCallback(async (files: File[]) => {
    // 複数ファイルを読み込むためにPromise.allでloadVideoFileを並列実行する
    try {
      const newItems = await Promise.all(files.map(loadVideoFile));
      setItems((prev) => [...prev, ...newItems]);
    } catch {
      console.error("動画の読み込みに失敗しました");
    }
  }, []);

  /**
   * ファイルを個別削除
   */
  const removeFile = useCallback((index: number) => {
    setItems((prev) => {
      // メモリリークを防ぐためにrevokeObjectURLでURLを解放
      URL.revokeObjectURL(prev[index].previewUrl);
      // 指定されたインデックス以外のファイルを返す
      return prev.filter((_, i) => i !== index);
    });
  }, []);

  /**
   * 全てのファイルを削除
   */
  const removeAllFiles = useCallback(() => {
    setItems((prev) => {
      // 全てのURLを解放
      prev.forEach((item) => URL.revokeObjectURL(item.previewUrl));
      return [];
    });
  }, []);

  /**
   * 個別アイテムの状態を更新する関数
   */
  const updateItem = useCallback((itemId: string, patch: Partial<ItemState>) => {
    setItems((prev) => {
      return prev.map((item) => {
        // 更新対象のアイテムIDと一致するアイテムに対して、patchの内容をマージして新しいオブジェクトを返す
        return item.id === itemId ? { ...item, ...patch } : item;
      });
    });
  }, []);

  /**
   * メタデータを更新する関数
   */
  const updateItemMetadata = useCallback((itemId: string, patch: Partial<UploadMetadata>) => {
    setItems((prev) => {
      return prev.map((item) => {
        // 更新対象のアイテムIDと一致するアイテムに対して、patchの内容をマージして新しいオブジェクトを返す
        return item.id === itemId ? { ...item, metadata: { ...item.metadata, ...patch } } : item;
      });
    });
  }, []);

  /**
   * すべてのアイテムのメタデータを一括更新する関数
   */
  const updateAllMetadata = useCallback((patch: Partial<UploadMetadata>) => {
    setItems((prev) => {
      return prev.map((item) => ({
        ...item,
        metadata: { ...item.metadata, ...patch },
      }));
    });
  }, []);

  // アンマウント時にもメモリリーク防止のため全URLを解放
  useEffect(() => {
    return () => {
      itemsRef.current.forEach((item) => URL.revokeObjectURL(item.previewUrl));
    };
  }, []);

  return {
    items,
    setImageAndUrl,
    setVideoAndUrl,
    removeFile,
    removeAllFiles,
    updateItem,
    updateItemMetadata,
    updateAllMetadata,
  };
};

/**
 * 画像ファイルを読み込んでプレビューURLと縦横サイズを取得する関数
 */
const loadImageFile = (file: File): Promise<UploadMedia> => {
  return new Promise((resolve, reject) => {
    // ファイルから一時的なURLを生成
    const previewUrl = URL.createObjectURL(file);
    const img = new Image();

    // 画像が読み込まれたら、ファイルとURL、縦横サイズをオブジェクトとして返す
    img.onload = () => {
      resolve({
        id: crypto.randomUUID(),
        file,
        previewUrl,
        width: img.naturalWidth,
        height: img.naturalHeight,
        status: "idle",
        progress: 0,
        metadata: {
          takenAt: new Date().toLocaleDateString("sv-SE"),
        },
      });
    };

    img.onerror = () => {
      // エラーが発生したらURLを解放してエラーを返す
      URL.revokeObjectURL(previewUrl);
      reject(new Error(`画像の読み込みに失敗しました: ${file.name}`));
    };

    // URLをセットすることで読み込み開始
    img.src = previewUrl;
  });
};

const loadVideoFile = (file: File): Promise<UploadMedia> => {
  return new Promise((resolve, reject) => {
    const previewUrl = URL.createObjectURL(file);
    const video = document.createElement("video");

    video.onloadedmetadata = () => {
      resolve({
        id: crypto.randomUUID(),
        file,
        previewUrl,
        width: video.videoWidth,
        height: video.videoHeight,
        status: "idle",
        progress: 0,
        metadata: {
          takenAt: new Date().toLocaleDateString("sv-SE"),
        },
      });
    };

    video.onerror = () => {
      URL.revokeObjectURL(previewUrl);
      reject(new Error(`動画の読み込みに失敗しました: ${file.name}`));
    };

    video.src = previewUrl;
  });
};
