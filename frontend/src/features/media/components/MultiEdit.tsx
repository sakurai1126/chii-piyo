"use client";

import Image from "next/image";
import { useState } from "react";

import { Button } from "@/components/ui/Button";
import { SharingGroupsSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";

import { AccordionContent } from "../../../components/ui/AccordionContent";

// ダミーデータ
const dummyTags = [
  { id: 1, name: "お出かけ", createdAt: new Date() },
  { id: 2, name: "誕生日", createdAt: new Date() },
];

const dummySharingGroups = [
  {
    id: 1,
    name: "家族全員",
    members: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    id: 2,
    name: "夫婦のみ",
    members: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];

type Props = {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
};

export const MultiEdit = ({ isOpen, setIsOpen }: Props) => {
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>(undefined);

  return (
    <>
      <Button
        className="-mt-10 flex items-center justify-center gap-2 max-md:w-30"
        onClick={() => setIsOpen(!isOpen)}
        variant={isOpen ? "cancel" : "primary"}
      >
        {!isOpen && <Image src="/images/select-start-check.svg" alt="" width={18} height={18} />}

        <p className="max-md:text-xs">{isOpen ? "閉じる" : "選択を開始"}</p>
      </Button>
      <AccordionContent isOpen={isOpen}>
        <div className="bg-white-back border-brown-dark mt-8 rounded-xl border px-8 py-6 max-md:mt-4 max-md:px-4 max-md:pt-4">
          <p className="border-line-gray w-fit border-b pb-2 text-xl font-medium max-md:text-sm">
            選択したアイテムを一括で編集する
          </p>
          {/* タグを編集 */}
          <TagSelector
            tags={dummyTags}
            isLoading={false}
            error={null}
            onRefresh={() => {}}
            selectedTagIds={selectedTagIds}
            onTagSelect={(tagIds) => setSelectedTagIds(tagIds)}
          />
          {/* 共有範囲を編集 */}
          <SharingGroupsSelector
            sharingGroups={dummySharingGroups}
            isLoading={false}
            error={null}
            onRefresh={() => {}}
            onSharingGroupSelect={(id) => setSelectedGroupId(id)}
            selectedGroupId={selectedGroupId}
          />
          {/* ボタン */}
          <div className="mt-8 flex items-end justify-between gap-5 max-md:flex-col max-md:items-start">
            <Button variant="primary" onClick={() => {}}>
              変更する
            </Button>
            <button className="text-warning text-xs underline max-md:ml-auto">
              選択したアイテムをすべて削除する
            </button>
          </div>
        </div>
      </AccordionContent>
    </>
  );
};
