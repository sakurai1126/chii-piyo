"use client";

import { useId } from "react";

import { SharingGroupResponseDto } from "@/lib/api-client/gen";

type Props = {
  // 表示する共有グループ一覧
  sharingGroups: SharingGroupResponseDto[];
  // 共有グループ選択時のコールバック
  onSharingGroupSelect: (selectedGroupId: number | undefined) => void;
  // 現在選択されている共有グループID
  selectedGroupId?: number;
};
export const SharingGroupsSelector = ({
  sharingGroups,
  onSharingGroupSelect,
  selectedGroupId,
}: Props) => {
  const uid = useId();

  return (
    <>
      <p className="mt-8 @max-md:mt-4 @max-md:text-[13px]">共有範囲を編集</p>

      {/* 共有グループ0件の表示 */}
      {sharingGroups.length === 0 && (
        <p className="mt-3 mr-10 text-sm @max-md:text-xs">共有グループがありません</p>
      )}

      {/* 共有グループ選択 */}
      <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2">
        {/* 共有グループ指定なし（全員公開） */}
        <label htmlFor={`${uid}-all`} className="flex items-center gap-2">
          <input
            type="radio"
            id={`${uid}-all`}
            name={`${uid}-sharing`}
            className="accent-accent-pink h-4 w-4"
            onChange={() => onSharingGroupSelect(undefined)}
            checked={selectedGroupId === undefined}
          />
          <p className="@max-md:text-[13px]">全員に公開</p>
        </label>
        {/* 登録されている共有グループ */}
        {sharingGroups?.map((group, index) => (
          <label key={group.id} htmlFor={`${uid}-${index}`} className="flex items-center gap-2">
            <input
              type="radio"
              id={`${uid}-${index}`}
              name={`${uid}-sharing`}
              className="accent-accent-pink h-4 w-4"
              onChange={() => onSharingGroupSelect(group.id)}
              checked={selectedGroupId === group.id}
            />
            <p className="@max-md:text-[13px]">{group.name}</p>
          </label>
        ))}
      </div>
    </>
  );
};
