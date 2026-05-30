"use client";

import Image from "next/image";
import { useState } from "react";

import { Button } from "@/components/ui/Button";
import { UserResponseDto } from "@/lib/api-client/gen";

import profile from "../assets/profile.svg";
import { useIconUpdate } from "../hooks/useIconUpdate";

type Props = {
  currentUser: UserResponseDto;
};

export const Profile = ({ currentUser }: Props) => {
  const [user, setUser] = useState<UserResponseDto>(currentUser);
  const { inputRef, previewUrl, handleChange, cancelEdit, upload } = useIconUpdate(setUser);

  return (
    <div className="mt-10 max-md:mt-8" id="profile">
      <div className="flex items-center gap-2">
        <Image src={profile} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        <p className="font-medium max-md:text-[13px]">プロフィール</p>
      </div>
      <div className="bg-white-back border-brown-dark mt-4 rounded-lg border py-6 pr-4 pl-10 max-md:mt-3 max-md:p-5">
        <div className="flex gap-5 max-md:flex-col max-md:items-center">
          <div className="w-fit">
            <div className="mx-auto h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
              <Image
                src={user.presignedIconUrl || "/images/no-image.svg"}
                alt=""
                width={80}
                height={80}
                className="aspect-square h-full w-full rounded-full object-cover"
              />
            </div>
            <button
              className="mt-2 cursor-pointer text-xs text-nowrap underline transition-all hover:opacity-70 max-md:text-[10px]"
              onClick={() => inputRef.current?.click()}
            >
              アイコンを編集する
            </button>
          </div>
          <div className="w-full">
            <div className="flex items-center gap-6 max-md:gap-4">
              <p className="max-md:text-[13px]">表示名：{user.displayName}</p>
              <button className="text-xs underline max-md:text-[10px]">編集する</button>
            </div>
            <p className="mt-2 max-md:mt-1 max-md:text-[13px]">メールアドレス：{user.email}</p>
            <p className="mt-8 ml-auto text-right text-xs max-md:mt-4 max-md:ml-0 max-md:text-left max-md:text-[10px]">
              メールアドレス、パスワードの変更は連絡してください
            </p>
          </div>
        </div>

        <div className={`mt-8 ${previewUrl ? "" : "hidden"}`}>
          <p className="text-sm font-medium">アップロードするアイコン</p>
          <div className="mt-4 flex items-center gap-2">
            {previewUrl && (
              <div className="h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <Image
                  src={previewUrl}
                  alt=""
                  width={80}
                  height={80}
                  className="aspect-square h-full w-full rounded-full object-cover"
                />
              </div>
            )}
            <input
              type="file"
              accept="image/*"
              className="text-sm file:hidden"
              ref={inputRef}
              onChange={(e) => handleChange(e.target.files?.[0])}
            />
          </div>
          <div className="mt-5 flex gap-4">
            <Button variant="cancel" onClick={cancelEdit}>
              キャンセル
            </Button>
            <Button className="block" onClick={upload}>
              送信する
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};
