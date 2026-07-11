"use client";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { updateMediaAction } from "@/features/media/actions/updateMediaAction"; // index.ts経由だと不要なサーバー処理を巻き込みエラーになるため直接ファイル指定
import { MediaResponseDto, SharingGroupResponseDto, UserResponseDto } from "@/lib/api-client/gen";

import { SharingGroupsSelector } from "./SharingGroupsSelector";

type Props = {
  isAdmin: boolean;
  media: MediaResponseDto;
  sharingGroups: SharingGroupResponseDto[];
  users: UserResponseDto[];
};

export const ShareGroupMediaDetail = ({ isAdmin, media, sharingGroups, users }: Props) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const currentSharingGroup = sharingGroups.find((group) => group.id === media.sharingGroupId);
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>(
    currentSharingGroup?.id,
  );

  const saveUpdate = async () => {
    if (selectedGroupId === currentSharingGroup?.id) {
      toast.error("変更されていません");
      return;
    }

    const result = await updateMediaAction({
      mediaId: media.id,
      sharingGroupId: selectedGroupId ?? null,
    });

    if (result.success) {
      toast.success("共有範囲を更新しました");
      setIsOpen(false);
    } else {
      toast.error(result.error);
    }
  };

  return (
    <div className="mt-7">
      <p className="max-md:text-sm">共有範囲</p>
      <div className="mt-2.5 flex items-center justify-between max-md:flex-col max-md:items-start">
        {currentSharingGroup ? (
          <div className="flex items-center gap-2">
            <p className="text-sm max-md:text-xs">{currentSharingGroup.name}</p>
            <div className="bg-line-gray h-px w-7"></div>
            {currentSharingGroup.members.map((member) => (
              <div
                key={member.id}
                className="h-7.5 w-7.5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
              >
                <Image
                  src={member.presignedIconUrl || "/images/no-image.svg"}
                  alt=""
                  width={29}
                  height={29}
                  className="h-full w-full rounded-full object-cover"
                />
              </div>
            ))}
          </div>
        ) : (
          <div className="flex items-center gap-2">
            <p className="text-sm max-md:text-xs">全員に公開</p>
            <div className="bg-line-gray h-px w-7"></div>
            {users.map((user) => (
              <div
                key={user.id}
                className="h-7.5 w-7.5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
              >
                <Image
                  src={user.presignedIconUrl || "/images/no-image.svg"}
                  alt=""
                  width={29}
                  height={29}
                  className="h-full w-full rounded-full object-cover"
                />
              </div>
            ))}
          </div>
        )}

        {isAdmin && (
          <button
            className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:mt-3 max-md:ml-auto max-md:text-xs"
            onClick={() => setIsOpen(true)}
          >
            共有範囲を変更する
          </button>
        )}
      </div>
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsOpen(false)}>
              <div className="flex h-full flex-col justify-between">
                <div className="-mt-8">
                  <SharingGroupsSelector
                    sharingGroups={sharingGroups}
                    onSharingGroupSelect={(id) => setSelectedGroupId(id)}
                    selectedGroupId={selectedGroupId}
                  />
                </div>
                <div className="flex justify-center gap-5 max-md:mt-8">
                  <Button variant="cancel" onClick={() => setIsOpen(false)}>
                    キャンセル
                  </Button>
                  <Button onClick={saveUpdate}>保存する</Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
