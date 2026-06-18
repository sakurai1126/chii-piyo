"use client";

import { useState } from "react";

import { Button } from "@/components/ui/Button";

import { useCreateAlbum } from "../hooks/useCreateAlbum";

type Props = {
  onAlbumCreated?: () => void;
};

export const AlbumAddForm = ({ onAlbumCreated }: Readonly<Props>) => {
  const [albumTitle, setAlbumTitle] = useState<string>("");

  // 追加成功時の処理
  const onSuccess = () => {
    // アルバム作成成功時のコールバックを呼び出す
    onAlbumCreated?.();
    // 入力値をリセット
    setAlbumTitle("");
  };

  const { createAlbum, isCreating } = useCreateAlbum({ onSuccess });

  // アルバム追加ボタンのクリックハンドラー
  const handleAddAlbum = () => {
    createAlbum(albumTitle);
  };
  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">アルバムを新しく追加する</p>
      <div className="mt-2 flex items-center gap-5">
        <input
          className="border-line-gray focus:outline-brown-light h-12 w-90 max-w-full rounded-sm border bg-white p-3 max-md:h-9 max-md:text-[13px]"
          type="text"
          value={albumTitle}
          onChange={(e) => setAlbumTitle(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleAddAlbum()}
        />

        <Button className="max-w-20 max-md:max-h-8" onClick={handleAddAlbum} disabled={isCreating}>
          追加
        </Button>
      </div>
    </>
  );
};
