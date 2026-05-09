"use client";

import { useId } from "react";

import ReadError from "@/components/ui/ReadError";
import { type TagResponseDto } from "@/lib/api-client/gen";

type Props = {
  // 表示するタグ一覧
  tags: TagResponseDto[];
  // タグ取得中フラグ
  isLoading?: boolean;
  // タグ取得失敗時のエラーメッセージ
  error?: string | null;
  // 取得失敗時の再試行
  onRetry?: () => void;
};

export const TagSelector = ({ tags, isLoading = false, error = null, onRetry }: Props) => {
  const uid = useId();

  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">タグを編集</p>

      {/* エラー時は再試行ボタンを表示 */}
      {!isLoading && error && <ReadError error={error} onRetry={onRetry} />}

      {/* 読み込み完了後 */}
      {!isLoading && !error && (
        <>
          {/* タグ0件の表示 */}
          {tags.length === 0 && (
            <p className="mt-3 mr-10 text-sm max-md:text-xs">タグがありません</p>
          )}

          {/* タグ一覧 */}
          {tags.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2">
              {tags.map((tag) => {
                const inputId = `${uid}-${tag.id}`;

                return (
                  <label key={tag.id} htmlFor={inputId} className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      id={inputId}
                      name={`${uid}-tag`}
                      className="accent-accent-pink h-4 w-4"
                    />
                    <p className="max-md:text-[13px]">{tag.name}</p>
                  </label>
                );
              })}
            </div>
          )}
        </>
      )}
    </>
  );
};
