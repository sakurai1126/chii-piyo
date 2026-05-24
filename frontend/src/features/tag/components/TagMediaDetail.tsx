"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { TagResponseDto } from "@/lib/api-client/gen";

import plus from "../assets/plus.svg";

import { TagSelector } from "./TagSelector";

type Props = {
  tags: TagResponseDto[] | undefined;
};

// ダミーデータ
const dummyTags = [
  { id: 1, name: "お出かけ", createdAt: new Date() },
  { id: 2, name: "誕生日", createdAt: new Date() },
];

export const TagMediaDetail = ({ tags }: Props) => {
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [isOpen, setIsOpen] = useState<boolean>(false);

  return (
    <div className="mt-7 max-md:mt-4">
      <p className="max-md:text-sm">タグ</p>

      <div className="mt-3 flex flex-wrap gap-3">
        {tags?.map((tag) => (
          <p
            key={tag.id}
            className="bg-accent-orange-back border-brown-middle text-brown-middle grid place-content-center rounded-2xl border px-4 py-1 text-sm max-md:px-3 max-md:text-xs"
          >
            {tag.name}
          </p>
        ))}
        <button
          className="border-line-gray text-note-gray hover:bg-line-gray flex cursor-pointer items-center gap-1 rounded-2xl border border-dashed bg-[rgba(255,255,255,0.7)] px-3 py-1 text-sm transition-all hover:text-white max-md:text-xs"
          onClick={() => setIsOpen(true)}
        >
          <Image src={plus} alt="" width={14} height={14} className="max-md:h-3 max-md:w-3" />
          <p>編集</p>
        </button>
      </div>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsOpen(false)}>
              <div className="flex h-full flex-col justify-between">
                <div className="-mt-8">
                  <TagSelector
                    tags={dummyTags}
                    isLoading={false}
                    error={null}
                    onRefresh={() => {}}
                    selectedTagIds={selectedTagIds}
                    onTagSelect={(tagIds) => setSelectedTagIds(tagIds)}
                  />
                </div>

                <div className="flex justify-center gap-5">
                  <Button variant="cancel" onClick={() => setIsOpen(false)}>
                    キャンセル
                  </Button>
                  <Button>保存する</Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
