"use client";

import Image from "next/image";
import { useState, useTransition } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { SharingGroupResponseDto, UserResponseDto } from "@/lib/api-client/gen";

import { deleteSharingGroupAction } from "../../actions/deleteSharingGroupAction";
import { updateSharingGroupAction } from "../../actions/updateSharingGroupAction";

type Props = {
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
export const SharingGroupListItem = ({ users, sharingGroup }: Props) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isDeleteConfirm, setIsDeleteConfirm] = useState<boolean>(false);
  const [newGroupName, setNewGroupName] = useState<string>(sharingGroup.name);
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
    setNewGroupName(sharingGroup.name);
    setIsMembers(sharingGroup.members.map((member) => member.userId));
  };

  // 共有グループを更新する
  const saveUpdateAction = () => {
    if (!newGroupName.trim()) {
      toast.error("グループ名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await updateSharingGroupAction({
        groupId: sharingGroup.id,
        name: newGroupName,
        userIds: isMembers,
      });

      if (result.success) {
        setIsOpen(false);
        toast.success("共有グループの編集に成功しました");
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
    <div className="border-brown-dark/50 border-t px-8 py-4 @max-lg:px-4 @max-md:flex-col @max-md:items-start @max-md:px-5">
      <div>
        <div className="flex items-center justify-between">
          <div className="flex items-center @max-md:flex-col @max-md:items-start">
            <p className="w-25 shrink-0 @max-md:text-[13px]">{sharingGroup.name}</p>
            <div className="ml-8 flex flex-wrap gap-x-6 gap-y-2 @max-md:mt-3 @max-md:ml-0 @max-md:gap-x-3">
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
                  <p className="@max-md:text-[13px]">{member.displayName}</p>
                </div>
              ))}
            </div>
          </div>
          <div className="flex shrink-0 gap-5 @max-md:mt-3 @max-md:ml-auto">
            {!isOpen && (
              <button
                type="button"
                className="cursor-pointer text-sm underline transition-all hover:opacity-70 @max-md:text-[10px]"
                onClick={() => setIsOpen(true)}
                disabled={isPending}
              >
                編集
              </button>
            )}

            <button
              type="button"
              className="text-warning cursor-pointer text-sm underline transition-all hover:opacity-70 @max-md:text-[10px] dark:font-medium"
              onClick={() => setIsDeleteConfirm(true)}
              disabled={isPending}
            >
              削除
            </button>
          </div>
        </div>
        <AccordionContent isOpen={isOpen}>
          <p className="pt-5">共有範囲グループ名の編集</p>
          <input
            className="border-line-gray focus:outline-brown-light bg-light-dark mt-2 block h-10 w-full max-w-90 rounded-sm border px-2.5 dark:outline-none"
            onChange={(e) => setNewGroupName(e.target.value)}
            value={newGroupName}
            disabled={isPending}
          />
          <p className="mt-3">メンバーの編集</p>
          <div className="mt-2 flex flex-wrap gap-6">
            {users.map((user) => (
              <label
                className="flex cursor-pointer items-center gap-2"
                key={user.id}
                htmlFor={`${sharingGroup.id}-${user.id}`}
              >
                <input
                  type="checkbox"
                  className="accent-accent-pink h-4 w-4"
                  id={`${sharingGroup.id}-${user.id}`}
                  checked={isMembers.includes(user.id)}
                  onChange={() => toggleMember(user.id)}
                  disabled={isPending}
                />

                <div className="h-6 w-6 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                  <Image
                    src={user.presignedIconUrl || "/images/no-image.svg"}
                    alt=""
                    width={24}
                    height={24}
                    className="aspect-square h-full w-full rounded-full object-cover"
                  />
                </div>
                <p className="@max-md:text-xs">{user.displayName}</p>
              </label>
            ))}
          </div>
          <div className="mt-4 flex gap-4">
            <Button variant="cancel" onClick={cancelEdit} disabled={isPending}>
              キャンセル
            </Button>
            <Button onClick={saveUpdateAction} disabled={isPending}>
              保存
            </Button>
          </div>
        </AccordionContent>

        <ConfirmModal
          isOpen={isDeleteConfirm}
          isPending={isPending}
          action={deleteAction}
          closeAction={() => setIsDeleteConfirm(false)}
          message={`共有グループ【${sharingGroup.name}】を削除します。`}
          buttonType="remove"
          buttonMessage="削除する"
        />
      </div>
    </div>
  );
};
