import Image from "next/image";

import { SettingsSharingGroups } from "@/features/sharing/components/SettingsSharingGroups";
import { UserResponseDto, SharingGroupResponseDto } from "@/lib/api-client/gen";

import sharingGroupsIcon from "../assets/sharing-groups.svg";

type Props = {
  users: UserResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
};

export const SharingGroups = ({ users, sharingGroups }: Props) => {
  return (
    <div className="mt-10 max-md:mt-8" id="sharing-groups">
      <div className="flex items-center gap-2">
        <Image
          src={sharingGroupsIcon}
          alt=""
          width={30}
          height={30}
          className="max-md:h-6 max-md:w-6"
        />
        <p className="font-medium max-md:text-[13px]">共有範囲の設定</p>
      </div>
      <SettingsSharingGroups users={users} sharingGroups={sharingGroups} />
    </div>
  );
};
