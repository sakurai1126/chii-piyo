"use client";

import Image from "next/image";

import { UserResponseDto, SharingGroupResponseDto } from "@/lib/api-client/gen";

import { NewSharingGroupEdit } from "./NewSharingGroupEdit";
import { SharingGroupListItem } from "./SharingGroupListItem";

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
          <SharingGroupListItem users={users} sharingGroup={sharingGroup} key={sharingGroup.id} />
        ))}
      </div>
      <NewSharingGroupEdit users={users} />
    </>
  );
};
