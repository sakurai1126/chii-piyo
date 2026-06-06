"use client";

import { AnimatePresence } from "motion/react";
import Image from "next/image";
import { useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { AccordionContent } from "@/components/ui/AccordionContent";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { UserResponseDto, SharingGroupResponseDto } from "@/lib/api-client/gen";

import { deleteSharingGroupAction } from "../actions/deleteSharingGroupAction";
import { editGroupMembersAction } from "../actions/editGroupMembersAction";

type Props = {
  users: UserResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
};

export const SettingsSharingGroups = ({ users, sharingGroups }: Props) => {
  return (
    <>
      <div className="bg-white-back border-brown-dark mt-4 rounded-lg border max-md:mt-3">
        {/* デフォルト 全員公開 */}
        <div className="flex items-center justify-between px-8 py-4 max-lg:px-4 max-md:flex-col max-md:items-start max-md:px-5">
          <div className="flex items-center max-md:flex-col max-md:items-start">
            <p className="w-25 shrink-0 max-md:text-[13px]">全員に公開</p>
            <div className="ml-8 flex flex-wrap gap-x-6 gap-y-2 max-md:mt-3 max-md:ml-0 max-md:gap-x-3">
              {users.map((user) => (
                <div className="flex items-center gap-2" key={user.id}>
                  <div className="h-10 w-10 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                    <Image
                      src={user.presignedIconUrl || "/images/no-image.svg"}
                      alt=""
                      width={40}
                      height={40}
                      className="aspect-square h-full w-full rounded-full object-cover"
                    />
                  </div>
                  <p className="max-md:text-[13px]">{user.displayName}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
        {sharingGroups.map((sharingGroup) => (
          <GroupContents users={users} sharingGroup={sharingGroup} key={sharingGroup.id} />
        ))}
      </div>
      <Button className="mt-5 ml-auto block max-md:mx-auto max-md:w-30">新規追加</Button>
    </>
  );
};

type GroupContentsProps = {
  users: UserResponseDto[];
  sharingGroup: SharingGroupResponseDto;
};
/**
 * アコーディオン要素をuseStateで個別管理するためコンポーネントとして切り出して管理
 *
 * @param users 全ユーザー
 * @param sharingGroup 共有範囲のグループ
 * @returns 共有範囲のグループ
 */
const GroupContents = ({ users, sharingGroup }: GroupContentsProps) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isDeleteConfirm, setIsDeleteConfirm] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 所属メンバーの状態管理
  const [isMembers, setIsMembers] = useState<number[]>(
    sharingGroup.members.map((member) => member.userId),
  );

  // 所属メンバーの切り替え
  const toggleMember = (userId: number) => {
    setIsMembers((prev) => {
      if (prev.includes(userId)) {
        return prev.filter((id) => id !== userId);
      }
      return [...prev, userId];
    });
  };

  // 編集をキャンセルする
  // 状態管理も初期状態に戻す
  const cancelEdit = () => {
    setIsOpen(false);
    setIsMembers(sharingGroup.members.map((member) => member.userId));
  };

  // サーバーアクションを呼び出して更新
  const saveEditAction = () => {
    startTransition(async () => {
      const result = await editGroupMembersAction({
        groupId: sharingGroup.id,
        userIds: isMembers,
      });

      if (result.success) {
        setIsOpen(false);
        setIsMembers(result.sharingGroup.members.map((member) => member.userId));

        toast.success("メンバーの編集に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  // 共有グループを削除する
  const deleteAction = () => {
    startTransition(async () => {
      const result = await deleteSharingGroupAction({
        groupId: sharingGroup.id,
      });

      if (result.success) {
        setIsDeleteConfirm(false);
        toast.success("共有グループの削除に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="border-brown-dark/50 border-t px-8 py-4 max-lg:px-4 max-md:flex-col max-md:items-start max-md:px-5">
      <div>
        <div className="flex items-center justify-between">
          <div className="flex items-center max-md:flex-col max-md:items-start">
            <p className="w-25 shrink-0 max-md:text-[13px]">{sharingGroup.name}</p>
            <div className="ml-8 flex flex-wrap gap-x-6 gap-y-2 max-md:mt-3 max-md:ml-0 max-md:gap-x-3">
              {sharingGroup.members.map((member) => (
                <div className="flex items-center gap-2" key={member.userId}>
                  <div className="h-10 w-10 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                    <Image
                      src={member.presignedIconUrl || "/images/no-image.svg"}
                      alt=""
                      width={40}
                      height={40}
                      className="aspect-square h-full w-full rounded-full object-cover"
                    />
                  </div>
                  <p className="max-md:text-[13px]">{member.displayName}</p>
                </div>
              ))}
            </div>
          </div>
          <div className="flex shrink-0 gap-5 max-md:mt-3 max-md:ml-auto">
            {!isOpen && (
              <button
                className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                onClick={() => setIsOpen(true)}
                disabled={isPending}
              >
                編集
              </button>
            )}

            <button
              className="text-warning cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
              onClick={() => setIsDeleteConfirm(true)}
              disabled={isPending}
            >
              削除
            </button>
          </div>
        </div>
        <AccordionContent isOpen={isOpen}>
          <p className="pt-5">グループの共有範囲を編集</p>
          <div className="mt-4 flex flex-wrap gap-6">
            {users.map((user) => (
              <label
                className="flex cursor-pointer items-center gap-2"
                key={user.id}
                htmlFor={`${sharingGroup.id}-${user.id}`}
              >
                {isOpen && (
                  <input
                    type="checkbox"
                    className="accent-accent-pink h-4 w-4"
                    id={`${sharingGroup.id}-${user.id}`}
                    defaultChecked={isMembers.includes(user.id)}
                    onChange={() => toggleMember(user.id)}
                  />
                )}

                <div className="h-6 w-6 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                  <Image
                    src={user.presignedIconUrl || "/images/no-image.svg"}
                    alt=""
                    width={24}
                    height={24}
                    className="aspect-square h-full w-full rounded-full object-cover"
                  />
                </div>
                <p className="max-md:text-xs">{user.displayName}</p>
              </label>
            ))}
          </div>
          <div className="mt-4 flex gap-4">
            <Button variant="cancel" onClick={cancelEdit} disabled={isPending}>
              キャンセル
            </Button>
            <Button onClick={saveEditAction} disabled={isPending}>
              保存
            </Button>
          </div>
        </AccordionContent>
        <AnimatePresence>
          {isDeleteConfirm && (
            <Modal>
              <ActionDialog onClose={isPending ? undefined : () => setIsDeleteConfirm(false)}>
                <div className="flex h-full flex-col justify-center">
                  <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                  <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                    共有グループ【{sharingGroup.name}】を削除します。
                    <br />
                    本当によろしいですか？
                  </p>
                  <div className="flex justify-center gap-5">
                    <Button
                      variant="cancel"
                      onClick={() => setIsDeleteConfirm(false)}
                      disabled={isPending}
                    >
                      キャンセル
                    </Button>
                    <Button variant="remove" onClick={deleteAction} disabled={isPending}>
                      削除する
                    </Button>
                  </div>
                </div>
              </ActionDialog>
            </Modal>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};
