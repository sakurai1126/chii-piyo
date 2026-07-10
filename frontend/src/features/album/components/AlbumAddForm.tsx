"use client";

import { useState, useTransition } from "react";

import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";

import { createAlbumAction } from "../actions/createAlbumAction";

export const AlbumAddForm = () => {
  const [albumTitle, setAlbumTitle] = useState<string>("");
  const [isPending, startTransition] = useTransition();

  // アルバム追加処理
  const addAlbumAction = () => {
    startTransition(async () => {
      const result = await createAlbumAction({ title: albumTitle });
      if (result.success) {
        setAlbumTitle("");
        toast.success("アルバムを追加しました");
      } else {
        toast.error(result.error);
      }
    });
  };
  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">アルバムを新しく追加する</p>
      <div className="mt-2 flex items-center gap-5">
        <input
          className="border-line-gray focus:outline-brown-light bg-light-dark h-12 w-90 max-w-full rounded-sm border p-3 max-md:h-9 max-md:text-[13px] dark:outline-none"
          type="text"
          value={albumTitle}
          onChange={(e) => setAlbumTitle(e.target.value)}
        />

        <Button className="max-w-20 max-md:max-h-8" onClick={addAlbumAction} disabled={isPending}>
          追加
        </Button>
      </div>
    </>
  );
};
