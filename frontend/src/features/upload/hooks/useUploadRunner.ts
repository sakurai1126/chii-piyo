"use client";

import { useCallback, useEffect, useRef, useState } from "react";

import { createMediaAction } from "../actions/createMediaAction";
import { updateMediaStatusAction } from "../actions/updateMediaStatusAction";
import { uploadToS3 } from "../lib/uploadToS3";
import { type UploadMedia, type UploadStatus, type ItemState } from "../types";

// 同時実行数の上限
// ブラウザのコネクション上限と帯域を考慮して3本に絞る
const MAX_CONCURRENCY = 3;

type UseUploadParams = {
  onItemUpdate?: (itemId: string, state: ItemState) => void;
  onAllComplete?: (result: { successCount: number; failedCount: number }) => void;
};

/**
 * アップロード処理を管理するフック
 *
 * @param onItemUpdate
 * 各ファイルのステータス変化を親に通知するためのコールバック関数
 * useUploadMediaStateのupdateItemを想定
 *
 * @param onAllComplete
 * 全件処理完了時のコールバック (成功・失敗の件数を渡す)
 *
 * @returns
 * - isUploading: アップロード処理中かどうかの状態
 * - upload: アップロード処理を実行する関数
 */
export const useUploadRunner = ({ onItemUpdate, onAllComplete }: UseUploadParams = {}) => {
  // ボタンの二重押下防止に使用するアップロード中かどうかのステータス
  const [isUploading, setIsUploading] = useState<boolean>(false);

  // 各ファイルの状態を管理するMap
  // Stateとして持つとレンダリングコストが上がるためrefで保持
  const itemStatesRef = useRef<Map<string, ItemState>>(new Map());

  // アンマウント時や明示的なキャンセル時に中断するためのAbortController
  const abortControllerRef = useRef<AbortController | null>(null);

  // アンマウント時に進行中のアップロードを中断
  useEffect(() => {
    return () => {
      abortControllerRef.current?.abort();
    };
  }, []);

  // アップロード中にタブを閉じると本体が届かないまま未完了のレコードが残るため、離脱前に確認を出す
  useEffect(() => {
    if (!isUploading) return;

    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault();
    };
    window.addEventListener("beforeunload", handleBeforeUnload);

    // next/linkによるクライアント遷移ではbeforeunloadが発火せず遷移するとアンマウントで中断されてしまう
    // App Routerには遷移イベントがないため、リンクのクリックをキャプチャ段階で拾ってNextのハンドラより先に止める
    const handleLinkClick = (event: MouseEvent) => {
      // 新規タブ等で開く操作や右クリックでのコンテキストメニューからの操作は現在のアップロードに影響しないため対象外
      if (event.defaultPrevented || event.button !== 0) return;
      if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;

      // クリックされた要素がリンクかどうかを判定する
      const anchor = (event.target as Element | null)?.closest?.("a[href]");
      if (!(anchor instanceof HTMLAnchorElement)) return;
      // リンクが新しいタブで開かれる場合やダウンロードリンクの場合は警告しない
      if ((anchor.target && anchor.target !== "_self") || anchor.hasAttribute("download")) return;

      // 同一ページ内のハッシュ移動などアンマウントを伴わない遷移は警告しない
      const destination = new URL(anchor.href, window.location.href);
      if (
        destination.origin === window.location.origin &&
        destination.pathname === window.location.pathname &&
        destination.search === window.location.search
      ) {
        return;
      }

      if (
        !window.confirm(
          "アップロード中です。ページを移動するとアップロードが中断されます。移動しますか?",
        )
      ) {
        event.preventDefault();
        event.stopPropagation();
      }
    };
    document.addEventListener("click", handleLinkClick, true);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
      document.removeEventListener("click", handleLinkClick, true);
    };
  }, [isUploading]);

  // 1ファイル分のアップロード処理
  // アップロード時はこの関数を並列実行する
  const uploadOneFile = useCallback(
    async (item: UploadMedia, signal: AbortSignal) => {
      // 状態更新用の関数
      const updateState = (partial: Partial<ItemState>) => {
        // 現在の状態を取得し、存在しない場合は初期状態を設定してから差分をマージして更新
        const current = itemStatesRef.current.get(item.id) ?? {
          status: "idle" as UploadStatus,
          progress: 0,
        };
        const next = { ...current, ...partial };
        itemStatesRef.current.set(item.id, next);
        // 状態更新のたびに親コンポーネントに通知
        onItemUpdate?.(item.id, next);
      };

      let mediaId: number | undefined;

      try {
        // ファイルのMIMEタイプからメディア種別を判定する
        const mediaType = (() => {
          switch (item.file.type.split("/")[0]) {
            case "image":
              return "PHOTO" as const;
            case "video":
              return "VIDEO" as const;
            default:
              throw new Error(`サポートされていないファイル形式です: ${item.file.type}`);
          }
        })();

        // Step 1: メタデータ登録 + 署名付きURL取得
        updateState({ status: "creating", progress: 0 });

        // メタデータ登録と署名付きURLの取得を同時に行うAPI呼び出し
        const createResult = await createMediaAction({
          mediaType,
          originalFilename: item.file.name,
          contentType: item.file.type,
          fileSize: item.file.size,
          width: item.width,
          height: item.height,
          takenAt: item.metadata.takenAt,
          albumId: item.metadata.albumId,
          sharingGroupId: item.metadata.sharingGroupId,
          tagIds: item.metadata.tagIds,
          comment: item.metadata.comment,
        });

        if (!createResult.success) {
          throw new Error(createResult.error);
        }

        // コメント・タグ登録の部分的な失敗はwarningとして記録
        if (createResult.data.warnings?.length) {
          console.warn(
            `[${item.file.name}] メタデータの一部登録に失敗:`,
            createResult.data.warnings,
          );
          updateState({ errorMessage: "メタデータの一部登録に失敗しました" });
        }

        // mediaIdは後続のステータス更新で必要になるため、先に変数に保持
        mediaId = createResult.data.mediaId;
        updateState({ status: "uploading", mediaId, progress: 0 });

        // Step 2: S3に直接アップロード
        await uploadToS3({
          presignedUrl: createResult.data.presignedUrl,
          file: item.file,
          // 進捗イベントのコールバックで進捗率を更新
          onProgress: (percent) => updateState({ progress: percent }),
          signal,
        });

        // Step 3: ステータスをCOMPLETEDに更新
        updateState({ status: "completing", progress: 100 });

        // バックエンドのステータスをCOMPLETEDに更新するAPIを呼び出し
        const statusResult = await updateMediaStatusAction({
          mediaId,
          uploadStatus: "COMPLETED",
        });

        if (!statusResult.success) {
          throw new Error(statusResult.error);
        }

        updateState({ status: "completed" });
        return { success: true };
      } catch (error) {
        const message = error instanceof Error ? error.message : "アップロードに失敗しました";
        console.error(`ファイル ${item.file.name} のアップロード失敗`, error);

        // mediaIdが採番済みでアップロードに失敗した場合は、バックエンド側のレコードをFAILEDに更新
        if (mediaId !== undefined && !signal.aborted) {
          await updateMediaStatusAction({
            mediaId,
            uploadStatus: "FAILED",
          }).catch((e) => {
            console.error("FAILED更新失敗", e);
          });
        }

        // 状態をFAILEDに更新して親コンポーネントに通知
        updateState({ status: "failed", errorMessage: message });
        return { success: false };
      }
    },
    [onItemUpdate],
  );

  /**
   * 同時実行数制御付きでファイルリストをアップロード
   *
   * @param items アップロード対象のファイル一覧
   * @param metadata 全ファイル共通のメタデータ
   */
  const upload = useCallback(
    async (items: UploadMedia[]) => {
      if (isUploading || items.length === 0) return;

      // アップロード開始フラグを立て、AbortControllerを初期化
      setIsUploading(true);
      const abortController = new AbortController();
      abortControllerRef.current = abortController;

      // 全件分のキュー、結果格納用のカウンタを作成
      const queue = [...items];
      let successCount = 0;
      let failedCount = 0;

      const worker = async () => {
        // キューから1件ずつ取り出してアップロード処理を実行
        while (queue.length > 0) {
          if (abortController.signal.aborted) return;

          // キューから1件取り出す
          // shift()は破壊的メソッドのため同時実行可能
          const item = queue.shift();
          if (!item) return;

          // アップロード処理を実行し、成功・失敗件数をカウント
          const result = await uploadOneFile(item, abortController.signal);

          if (result.success) {
            successCount++;
          } else {
            failedCount++;
          }
        }
      };

      // 上限数のワーカーを並列起動して全件処理を待つ
      const workers = Array.from(
        // 同時実行数の上限とファイル数を比較して小さい方をワーカー数にする
        { length: Math.min(MAX_CONCURRENCY, items.length) },
        // ワーカー関数を呼び出してPromiseの配列を作る
        () => worker(),
      );

      // 全ワーカーの完了を待つ
      await Promise.all(workers);

      // アップロード完了フラグを下ろし、後続のアップロードに備えてAbortControllerをリセット
      setIsUploading(false);
      abortControllerRef.current = null;

      // 完了時のコールバックに成功・失敗件数を渡してメッセージを表示
      // 件数に応じてuseUploadPageにてsetResultMessageが呼ばれる想定
      onAllComplete?.({ successCount, failedCount });
    },
    [isUploading, uploadOneFile, onAllComplete],
  );

  return { upload, isUploading };
};
