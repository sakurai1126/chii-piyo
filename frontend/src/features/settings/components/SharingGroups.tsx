import Image from "next/image";

import { SettingsSharingGroups } from "@/features/sharing/components/settings/SettingsSharingGroups";
import { UserResponseDto, SharingGroupResponseDto } from "@/lib/api-client/gen";

import icon from "../assets/sharing-groups.svg";
import whiteIcon from "../assets/white/sharing-groups.svg";

type Props = {
  users: UserResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
};

export const SharingGroups = ({ users, sharingGroups }: Props) => {
  return (
    <div className="mt-10 max-md:mt-8" id="sharing-groups">
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
        <p className="font-medium max-md:text-[13px]">共有範囲の設定</p>
      </div>
      <SettingsSharingGroups users={users} sharingGroups={sharingGroups} />
    </div>
  );
};
