import Image from "next/image";

import { SettingsTags } from "@/features/tag";
import { TagResponseDto } from "@/lib/api-client/gen";

import tagsIcon from "../assets/tags.svg";

type Props = {
  tags: TagResponseDto[];
};

export const Tags = ({ tags }: Props) => {
  return (
    <div className="mt-10 max-md:mt-8" id="tags">
      <div className="flex items-center gap-2">
        <Image src={tagsIcon} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        <p className="font-medium max-md:text-[13px]">タグの設定</p>
      </div>
      <SettingsTags tags={tags} />
    </div>
  );
};
