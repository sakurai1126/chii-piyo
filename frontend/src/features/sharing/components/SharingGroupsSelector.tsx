"use client";

import { useEffect, useId, useRef } from "react";

import ReadError from "@/components/ui/ReadError";
import { SharingGroupResponseDto } from "@/lib/api-client/gen";

type Props = {
  // 表示する共有グループ一覧
  sharingGroups: SharingGroupResponseDto[];
  // 共有グループ取得中フラグ
  isLoading?: boolean;
  // 共有グループ取得失敗時のエラーメッセージ
  error?: string | null;
  // 取得失敗時の再試行
  onRefresh?: () => void;
  // 共有グループ選択時のコールバック
  onSharingGroupSelect: (selectedGroupId: number) => void;
  // 現在選択されている共有グループID
  selectedGroupId?: number;
};
export const SharingGroupsSelector = ({
  sharingGroups,
  isLoading = false,
  error = null,
  onRefresh,
  onSharingGroupSelect,
  selectedGroupId,
}: Props) => {
  const uid = useId();

  // 親でuseCallbackなしに関数が渡されると毎レンダーで新インスタンスが生成され、不要なeffectが走る可能性がある
  // refで最新値を保持して参照する形にする
  const onSharingGroupSelectRef = useRef(onSharingGroupSelect);
  useEffect(() => {
    onSharingGroupSelectRef.current = onSharingGroupSelect;
  });

  // 共有グループが読み込まれたとき先頭グループを選択状態にする
  useEffect(() => {
    if (sharingGroups.length > 0) {
      onSharingGroupSelectRef.current(sharingGroups[0].id);
    }
  }, [sharingGroups]);

  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">共有範囲を編集</p>

      {/* エラー時は再試行ボタンを表示 */}
      {!isLoading && error && <ReadError error={error} onRefresh={onRefresh} />}

      {/* 読み込み完了後 */}
      {!isLoading && !error && (
        <>
          {/* 共有グループ0件の表示 */}
          {sharingGroups.length === 0 && (
            <p className="mt-3 mr-10 text-sm max-md:text-xs">共有グループがありません</p>
          )}

          {/* 共有グループ選択 */}
          {sharingGroups.length > 0 && (
            <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2">
              {sharingGroups.map((group, index) => (
                <label
                  key={group.id}
                  htmlFor={`${uid}-${index}`}
                  className="flex items-center gap-2"
                >
                  <input
                    type="radio"
                    id={`${uid}-${index}`}
                    name={`${uid}-sharing`}
                    className="accent-accent-pink h-4 w-4"
                    onChange={() => onSharingGroupSelect(group.id)}
                    checked={
                      selectedGroupId === undefined ? index === 0 : group.id === selectedGroupId
                    }
                  />
                  <p className="max-md:text-[13px]">{group.name}</p>
                </label>
              ))}
            </div>
          )}
        </>
      )}
    </>
  );
};
