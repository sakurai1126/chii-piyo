"use client";

import { useState } from "react";

import { Button } from "@/components/ui/Button";

import { useCreateTag } from "../hooks/useCreateTag";

type Props = {
  onTagCreated?: () => void;
};

export default function TagAddForm({ onTagCreated }: Readonly<Props>) {
  const [tagName, setTagName] = useState("");

  // 追加成功時の処理
  const onSuccess = () => {
    // タグ作成成功時のコールバックを呼び出す
    onTagCreated?.();
    // 入力値をリセット
    setTagName("");
  };

  const { createTag, isCreating, error } = useCreateTag({ onSuccess });

  // タグ追加ボタンのクリックハンドラー
  const handleAddTag = () => {
    createTag(tagName);
  };
  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">タグを新しく追加する</p>
      <div className="mt-2 flex items-center gap-5">
        <input
          className="border-line-gray focus:outline-brown-light bg-light-dark h-12 w-90 max-w-full rounded-sm border p-3 max-md:h-9 max-md:text-[13px] dark:outline-none"
          type="text"
          value={tagName}
          onChange={(e) => setTagName(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleAddTag()}
        />

        <Button className="max-w-20 max-md:max-h-8" onClick={handleAddTag} disabled={isCreating}>
          追加
        </Button>
      </div>
      {error && <p className="text-warning mt-2 text-sm dark:font-medium">{error}</p>}
    </>
  );
}
