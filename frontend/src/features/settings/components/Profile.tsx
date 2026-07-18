"use client";

import Image from "next/image";

import { Button } from "@/components/ui/Button";
import { UserResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import icon from "../assets/profile.svg";
import whiteIcon from "../assets/white/profile.svg";
import { useChangeSettings } from "../hooks/useChangeSettings";

type Props = {
  isEasy: boolean;
  currentUser: UserResponseDto;
};

export const Profile = ({ isEasy, currentUser }: Props) => {
  const {
    user,
    iconInputRef,
    previewUrl,
    iconEdit,
    cancelIconEdit,
    iconUpload,
    isNameChangeMode,
    setIsNameChangeMode,
    newName,
    setNewName,
    nameChange,
  } = useChangeSettings({ currentUser });

  return (
    <div className="mt-10 @max-md:mt-8" id="profile">
      <div className="flex items-center gap-2">
        <Image
          src={icon}
          alt=""
          width={30}
          height={30}
          className="@max-md:h-6 @max-md:w-6 dark:hidden"
        />
        <Image
          src={whiteIcon}
          alt=""
          width={30}
          height={30}
          className="hidden @max-md:h-6 @max-md:w-6 dark:block"
        />
        <p className={cn("font-medium", isEasy ? "text-[18px]" : "@max-md:text-[13px]")}>
          プロフィール
        </p>
      </div>
      <div className="bg-background-normal dark:bg-background-accent border-brown-dark mt-4 rounded-lg border py-6 pr-4 pl-10 @max-md:mt-3 @max-md:p-5">
        <div className="flex gap-5 @max-md:flex-col @max-md:items-center">
          <div className="w-fit">
            <div
              className={cn(
                "mx-auto shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px",
                isEasy ? "h-30 w-30" : "h-20 w-20",
              )}
            >
              <div className="rounded-full bg-white">
                <Image
                  src={user.presignedIconUrl || "/images/no-image.svg"}
                  alt=""
                  width={80}
                  height={80}
                  className="aspect-square h-full w-full rounded-full object-cover"
                />
              </div>
            </div>
            <button
              type="button"
              className={cn(
                "mt-2 cursor-pointer text-nowrap underline transition-all hover:opacity-70",
                isEasy ? "text-[13px] font-medium" : "text-xs @max-md:text-[10px]",
              )}
              onClick={() => iconInputRef.current?.click()}
            >
              アイコンを編集する
            </button>
          </div>
          <div className="w-full">
            <div>
              <div className={cn("flex", isEasy ? "flex-col" : "items-center gap-6 @max-md:gap-4")}>
                <p className={cn("text-[18px] font-medium", !isEasy && "@max-md:text-[13px]")}>
                  表示名：{user.displayName}
                </p>
                <button
                  type="button"
                  className={cn(
                    "cursor-pointer text-xs underline transition-all hover:opacity-70",
                    isEasy ? "mt-2 w-fit text-[13px]" : "@max-md:text-[10px]",
                  )}
                  onClick={() => setIsNameChangeMode(true)}
                >
                  編集する
                </button>
              </div>
              {isNameChangeMode && (
                <div className="mt-2">
                  <p className={isEasy ? "text-sm font-medium" : "@max-md:text-[13px]"}>
                    新しい表示名
                  </p>
                  <input
                    type="text"
                    className="border-line-gray focus:outline-brown-light bg-light-dark mt-2 block h-10 w-full max-w-90 rounded-sm border px-2.5 dark:outline-none"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                  />
                  <div className="mt-3 flex gap-3">
                    <Button variant="cancel" onClick={() => setIsNameChangeMode(false)}>
                      キャンセル
                    </Button>
                    <Button onClick={nameChange}>変更する</Button>
                  </div>
                </div>
              )}
            </div>

            <p
              className={cn(
                "mt-2 break-all",
                isEasy ? "text-[16px] font-medium" : "@max-md:mt-1 @max-md:text-[13px]",
              )}
            >
              メールアドレス：
              <br hidden={!isEasy} />
              {user.email}
            </p>
            <p
              className={cn(
                "mt-8 ml-auto text-right text-xs @max-md:ml-0 @max-md:text-left",
                isEasy ? "text-[11px] font-medium" : "@max-md:mt-4 @max-md:text-[10px]",
              )}
            >
              メールアドレス、パスワードの変更は連絡してください
            </p>
          </div>
        </div>

        <div className={cn("mt-8", !previewUrl && "hidden")}>
          <p className="text-sm font-medium">アップロードするアイコン</p>
          <div className="mt-4 flex items-center gap-2">
            {previewUrl && (
              <div className="h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <div className="rounded-full bg-white">
                  <Image
                    src={previewUrl}
                    alt=""
                    width={80}
                    height={80}
                    className="aspect-square h-full w-full rounded-full object-cover"
                  />
                </div>
              </div>
            )}
            <input
              type="file"
              accept="image/*"
              className="text-sm file:hidden"
              ref={iconInputRef}
              onChange={(e) => iconEdit(e.target.files?.[0])}
            />
          </div>
          <div className="mt-5 flex gap-4">
            <Button variant="cancel" onClick={cancelIconEdit}>
              キャンセル
            </Button>
            <Button className="block" onClick={iconUpload}>
              送信する
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};
