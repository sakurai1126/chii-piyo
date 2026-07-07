"use client";

import { useState, useTransition } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";

import { createAlbumAction } from "../../actions/createAlbumAction";

export const NewAlbumEdit = () => {
  const [isAlbumEdit, setIsAlbumEdit] = useState<boolean>(false);
  const [isPending, startTransition] = useTransition();
  const [newAlbumName, setNewAlbumName] = useState<string>("");

  const createAction = () => {
    if (!newAlbumName.trim()) {
      toast.error("アルバム名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await createAlbumAction({
        title: newAlbumName.trim(),
      });

      if (result.success) {
        setNewAlbumName("");
        setIsAlbumEdit(false);
        toast.success("アルバムの作成に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      <AccordionContent isOpen={isAlbumEdit}>
        <p className="mt-3 font-medium max-md:text-[13px]">新規アルバムの追加</p>

        <div className="bg-background-normal border-brown-dark mt-3 rounded-lg border px-8 py-4 max-md:mt-3">
          <p className="max-md:text-[13px]">アルバムの名前</p>
          <input
            className="border-line-gray focus:outline-brown-light bg-light-dark mt-2 block h-10 w-full max-w-90 rounded-sm border px-2.5 dark:outline-none"
            onChange={(e) => setNewAlbumName(e.target.value)}
            value={newAlbumName}
            disabled={isPending}
          />

          <div className="mt-4 flex gap-4">
            <Button variant="cancel" onClick={() => setIsAlbumEdit(false)} disabled={isPending}>
              キャンセル
            </Button>
            <Button onClick={createAction} disabled={isPending}>
              保存
            </Button>
          </div>
        </div>
      </AccordionContent>
      <AccordionContent isOpen={!isAlbumEdit}>
        <Button
          className="mt-5 ml-auto block max-md:mx-auto max-md:w-30"
          onClick={() => setIsAlbumEdit(true)}
        >
          新規追加
        </Button>
      </AccordionContent>
    </>
  );
};
