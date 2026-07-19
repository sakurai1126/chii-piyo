"use client";

import { useState, useTransition } from "react";

import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";

import { createTagAction } from "../actions/createTagAction";

export const TagAddForm = () => {
  const [tagName, setTagName] = useState<string>("");
  const [isPending, startTransition] = useTransition();

  // タグ追加処理
  const addTagAction = () => {
    startTransition(async () => {
      const result = await createTagAction({ name: tagName });
      if (result.success) {
        setTagName("");
        toast.success("タグを追加しました");
      } else {
        toast.error(result.error);
      }
    });
  };
  return (
    <>
      <p className="mt-8 @max-md:mt-4 @max-md:text-[13px]">タグを新しく追加する</p>
      <div className="mt-2 flex items-center gap-5">
        <input
          className="border-line-gray focus:outline-brown-light bg-light-dark h-12 w-90 max-w-full rounded-sm border p-3 @max-md:h-9 @max-md:text-[13px] dark:outline-none"
          type="text"
          value={tagName}
          onChange={(e) => setTagName(e.target.value)}
        />

        <Button className="max-w-20 @max-md:max-h-8" onClick={addTagAction} disabled={isPending}>
          追加
        </Button>
      </div>
    </>
  );
};
