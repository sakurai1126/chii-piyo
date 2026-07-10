"use client";

import Image from "next/image";
import { useState, useTransition } from "react";

import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { UserResponseDto, UserRoleUpdateRequestDtoRoleEnum } from "@/lib/api-client/gen";

import { updateUserRoleAction } from "../actions/updateUserRoleAction";
import roleChangeIcon from "../assets/role-change.svg";

// 共通クラス定義
const baseClass =
  "flex h-7 w-20 items-center justify-center rounded-3xl border text-xs font-medium";
const buttonClass = "group cursor-pointer transition-all hover:w-25 hover:gap-1 hover:text-white";

// 権限の表示/変更
type Props = {
  isAdmin: boolean;
  currentUser: UserResponseDto;
  user: UserResponseDto;
};

export const RoleTag = ({ isAdmin, currentUser, user }: Props) => {
  const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);

  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // タグを更新する
  const saveUpdateAction = (newRole: UserRoleUpdateRequestDtoRoleEnum) => {
    startTransition(async () => {
      const result = await updateUserRoleAction({
        userId: user.id,
        newRole,
      });

      if (result.success) {
        setIsConfirmOpen(false);
        toast.success("権限の変更に成功しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <>
      {isAdmin && (
        <>
          {/* 自分の場合変更不可にして表示のみ */}
          {user.role === "ADMIN" && currentUser.id === user.id && (
            <p className={`text-accent-pink bg-accent-pink-back ${baseClass}`}>管理者</p>
          )}

          {/* 他の管理者の場合変更可能 */}
          {user.role === "ADMIN" && currentUser.id !== user.id && (
            <div>
              <button
                className={`text-accent-pink border-accent-pink bg-accent-pink-back hover:bg-warning ${baseClass} ${buttonClass}`}
                onClick={() => setIsConfirmOpen(true)}
                disabled={isPending}
              >
                <span>管理者</span>
                <Image
                  src={roleChangeIcon}
                  alt=""
                  className="w-0 overflow-hidden group-hover:w-fit"
                  width={20}
                  height={20}
                />
              </button>

              <ConfirmModal
                isOpen={isConfirmOpen}
                isPending={isPending}
                action={() => saveUpdateAction(UserRoleUpdateRequestDtoRoleEnum.Viewer)}
                closeAction={() => setIsConfirmOpen(false)}
                message="指定ユーザーの権限を閲覧者に変更します"
                buttonMessage="変更する"
              />
            </div>
          )}

          {/* 閲覧者の場合 */}
          {user.role === "VIEWER" && (
            <div>
              <button
                className={`text-brown-middle border-brown-middle bg-accent-orange-back hover:bg-brown-dark ${baseClass} ${buttonClass}`}
                onClick={() => setIsConfirmOpen(true)}
                disabled={isPending}
              >
                <span>閲覧者</span>
                <Image
                  src={roleChangeIcon}
                  alt=""
                  className="w-0 overflow-hidden group-hover:w-fit"
                  width={20}
                  height={20}
                />
              </button>

              <ConfirmModal
                isOpen={isConfirmOpen}
                isPending={isPending}
                action={() => saveUpdateAction(UserRoleUpdateRequestDtoRoleEnum.Admin)}
                closeAction={() => setIsConfirmOpen(false)}
                message="指定ユーザーの権限を管理者に変更します"
                buttonMessage="変更する"
              />
            </div>
          )}
        </>
      )}

      {/* 閲覧者の場合表示のみ */}
      {!isAdmin && (
        <>
          {user.role === "ADMIN" && (
            <p className={`text-accent-pink bg-accent-pink-back ${baseClass}`}>管理者</p>
          )}

          {user.role === "VIEWER" && (
            <p className={`text-brown-middle bg-accent-orange-back ${baseClass}`}>閲覧者</p>
          )}
        </>
      )}
    </>
  );
};
