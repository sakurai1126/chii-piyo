import Image from "next/image";

import { SettingsTags } from "@/features/tag";
import { TagResponseDto } from "@/lib/api-client/gen";

import icon from "../assets/tags.svg";
import whiteIcon from "../assets/white/tags.svg";

type Props = {
  tags: TagResponseDto[];
};

export const Tags = ({ tags }: Props) => {
  return (
    <div className="mt-10 @max-md:mt-8" id="tags">
      <div className="flex items-center gap-2">
        <Image
          src={icon}
          alt=""
          width={30}
          height={30}
          className="max-md:w-6 @max-md:h-6 dark:hidden"
        />
        <Image
          src={whiteIcon}
          alt=""
          width={30}
          height={30}
          className="hidden @max-md:h-6 @max-md:w-6 dark:block"
        />
        <p className="font-medium @max-md:text-[13px]">タグの設定</p>
      </div>
      <SettingsTags tags={tags} />
    </div>
  );
};
