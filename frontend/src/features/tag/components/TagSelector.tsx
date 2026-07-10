"use client";

import { useId } from "react";

import { type TagResponseDto } from "@/lib/api-client/gen";

import TagAddForm from "./TagAddForm";

type Props = {
  // 表示するタグ一覧
  tags: TagResponseDto[];
  // タグ選択時のコールバック（タグIDの配列を渡す）
  onTagSelect: (selectedTagIds: number[]) => void;
  // 現在選択されているタグIDの配列
  selectedTagIds: number[];
  // タグ追加フォームの表示フラグ（省略時は非表示）
  addTag?: boolean;
};

export const TagSelector = ({ tags, onTagSelect, selectedTagIds, addTag = false }: Props) => {
  const uid = useId();

  const handleChange = (tagId: number, checked: boolean) => {
    const next = checked ? [...selectedTagIds, tagId] : selectedTagIds.filter((id) => id !== tagId);
    onTagSelect(next);
  };

  return (
    <div className="mt-8">
      <p className="max-md:mt-4 max-md:text-[13px]">タグを編集</p>

      {/* タグ一覧 */}
      {tags.length > 0 ? (
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
                  onChange={(e) => handleChange(tag.id, e.target.checked)}
                  checked={selectedTagIds.includes(tag.id)}
                />
                <p className="max-md:text-[13px]">{tag.name}</p>
              </label>
            );
          })}
        </div>
      ) : (
        <p className="mt-3 mr-10 text-sm max-md:text-xs">タグがありません</p>
      )}

      {/* タグ追加フォーム */}
      {addTag && <TagAddForm />}
    </div>
  );
};
