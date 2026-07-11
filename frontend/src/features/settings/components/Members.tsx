import Image from "next/image";

import { SharingGroupResponseDto, UserResponseDto } from "@/lib/api-client/gen";

import icon from "../assets/members.svg";
import whiteIcon from "../assets/white/members.svg";

import { RoleTag } from "./RoleTag";

type Props = {
  isAdmin: boolean;
  currentUser: UserResponseDto;
  users: UserResponseDto[];
  sharingGroups?: SharingGroupResponseDto[];
};

export const Members = async ({ isAdmin, currentUser, users, sharingGroups }: Props) => {
  const sharingGroupMap = new Map<number, string>();
  sharingGroups?.forEach((sharingGroup) => {
    sharingGroupMap.set(sharingGroup.id, sharingGroup.name);
  });

  return (
    <div className="mt-10 max-md:mt-8" id="members">
      <div className="flex items-center gap-2">
        <Image
          src={icon}
          alt=""
          width={30}
          height={30}
          className="max-md:h-6 max-md:w-6 dark:hidden"
        />
        <Image
          src={whiteIcon}
          alt=""
          width={30}
          height={30}
          className="hidden max-md:h-6 max-md:w-6 dark:block"
        />
        <p className="font-medium max-md:text-[13px]">メンバー一覧</p>
      </div>
      <div className="bg-background-normal dark:bg-background-accent border-brown-dark mt-4 rounded-lg border max-md:mt-3">
        {users.map((user, index) => (
          <div
            className={`py-4 pr-5 pl-7 max-md:p-5 ${index > 0 ? "border-brown-dark/50 border-t" : ""}`}
            key={user.id}
          >
            <div className="flex justify-between">
              <div className="flex items-center gap-6">
                <div className="h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
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
                <div className="grid gap-1">
                  <p className="text-sm max-md:text-[13px]">
                    表示名：
                    <br className="md:hidden" />
                    {user.displayName}
                  </p>
                  <p className="text-sm max-md:hidden">メールアドレス：{user.email}</p>
                  {isAdmin && (
                    <p className="text-sm max-md:hidden">
                      閲覧可能な共有範囲：
                      {user.scopeSharingGroups.map((sharingGroupId, index) => {
                        return (
                          <span key={sharingGroupId}>
                            {sharingGroupMap.get(sharingGroupId)}
                            {index < user.scopeSharingGroups.length - 1 ? "、" : ""}
                          </span>
                        );
                      })}
                    </p>
                  )}
                </div>
              </div>
              <RoleTag isAdmin={isAdmin} currentUser={currentUser} user={user} />
            </div>
            <div className="mt-2 grid gap-1 md:hidden">
              <p className="text-xs">メールアドレス：{user.email}</p>
              {isAdmin && (
                <p className="text-xs">
                  閲覧可能な共有範囲：
                  {user.scopeSharingGroups.map((sharingGroupId, index) => {
                    return (
                      <span key={sharingGroupId}>
                        {sharingGroupMap.get(sharingGroupId)}
                        {index < user.scopeSharingGroups.length - 1 ? "、" : ""}
                      </span>
                    );
                  })}
                </p>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
