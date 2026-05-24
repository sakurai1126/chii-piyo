"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";

import { SharingGroupsSelector } from "./SharingGroupsSelector";
export const ShareGroupMediaDetail = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>(undefined);
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
  return (
    <div className="mt-7">
      <p className="max-md:text-sm">共有範囲</p>
      <div className="mt-2.5 flex items-center justify-between max-md:flex-col max-md:items-start">
        <div className="flex items-center gap-2">
          <p className="text-sm max-md:text-xs">家族全員</p>
          <div className="bg-line-gray h-px w-7"></div>
          {[1, 2, 3, 4].map((item) => (
            <div
              key={item}
              className="h-7.5 w-7.5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
            >
              <Image
                src="/images/mock-img.jpg"
                alt=""
                width={29}
                height={29}
                className="h-full w-full rounded-full object-cover"
              />
            </div>
          ))}
        </div>
        <button
          className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:mt-3 max-md:ml-auto max-md:text-xs"
          onClick={() => setIsOpen(true)}
        >
          共有範囲を変更する
        </button>
      </div>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsOpen(false)}>
              <div className="flex h-full flex-col justify-between">
                <div className="-mt-8">
                  <SharingGroupsSelector
                    sharingGroups={dummySharingGroups}
                    isLoading={false}
                    error={null}
                    onRefresh={() => {}}
                    onSharingGroupSelect={(id) => setSelectedGroupId(id)}
                    selectedGroupId={selectedGroupId}
                  />
                </div>

                <div className="flex justify-center gap-5 max-md:mt-8">
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
