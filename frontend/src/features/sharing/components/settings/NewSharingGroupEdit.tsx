"use client";

import Image from "next/image";
import { useState, useTransition } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { UserResponseDto } from "@/lib/api-client/gen";

import { createGroupAction } from "../../actions/createGroupAction";

type Props = {
  users: UserResponseDto[];
};
/**
 * 新規グループ追加用のコンポーネントUI
 * @param users 全ユーザー
 * @returns 新規グループ追加UI
 */
export const NewSharingGroupEdit = ({ users }: Props) => {
  const [isNewGroupEdit, setIsNewGroupEdit] = useState<boolean>(false);
  const [isPending, startTransition] = useTransition();
  const [newGroupName, setNewGroupName] = useState<string>("");
  const [newGroupMemberIds, setNewGroupMemberIds] = useState<number[]>([]);

  const toggleMember = (userId: number) => {
    setNewGroupMemberIds((prev) =>
      prev.includes(userId) ? prev.filter((id) => id !== userId) : [...prev, userId],
    );
  };

  const createAction = () => {
    if (!newGroupName.trim()) {
      toast.error("グループ名を入力してください");
      return;
    }

    startTransition(async () => {
      const result = await createGroupAction({
        name: newGroupName,
        userIds: newGroupMemberIds,
      });

      if (result.success) {
        setNewGroupName("");
        setNewGroupMemberIds([]);
        setIsNewGroupEdit(false);
        toast.success("共有グループの作成に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      <AccordionContent isOpen={isNewGroupEdit}>
        <p className="mt-3 font-medium @max-md:text-[13px]">新規グループの追加</p>

        <div className="bg-background-normal border-brown-dark mt-3 rounded-lg border px-8 py-4 @max-md:mt-3">
          <p className="@max-md:text-[13px]">共有グループの名前</p>
          <input
            className="border-line-gray focus:outline-brown-light bg-light-dark mt-2 block h-10 w-full max-w-90 rounded-sm border px-2.5 dark:outline-none"
            onChange={(e) => setNewGroupName(e.target.value)}
            value={newGroupName}
            disabled={isPending}
          />
          <p className="mt-2 @max-md:text-[13px]">追加するメンバー</p>
          <div className="mt-4 flex flex-wrap gap-6">
            {users.map((user) => (
              <label
                className="flex cursor-pointer items-center gap-2"
                key={user.id}
                htmlFor={`new-${user.id}`}
              >
                <input
                  type="checkbox"
                  className="accent-accent-pink h-4 w-4"
                  id={`new-${user.id}`}
                  onChange={() => toggleMember(user.id)}
                  checked={newGroupMemberIds.includes(user.id)}
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
            <Button variant="cancel" onClick={() => setIsNewGroupEdit(false)} disabled={isPending}>
              キャンセル
            </Button>
            <Button onClick={createAction} disabled={isPending}>
              保存
            </Button>
          </div>
        </div>
      </AccordionContent>
      <AccordionContent isOpen={!isNewGroupEdit}>
        <Button
          className="mt-5 ml-auto block @max-md:mx-auto @max-md:w-30"
          onClick={() => setIsNewGroupEdit(true)}
        >
          新規追加
        </Button>
      </AccordionContent>
    </>
  );
};
