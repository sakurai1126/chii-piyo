import Image from "next/image";
import { useId } from "react";

import { Button } from "@/components/ui/Button";
import { TrashItemResponseDto } from "@/lib/api-client/gen";
import { calculateRemainingDays, formatJapaneseDateNonTime } from "@/utils/date";

type Props = {
  trashItem: TrashItemResponseDto;
};
export const TrashItem = ({ trashItem }: Props) => {
  const uid = useId();
  const sizeInKB = (trashItem.media.fileSize / 1024).toFixed(0);
  const sizeInMB = (trashItem.media.fileSize / 1024 / 1024).toFixed(1);

  return (
    <div className="bg-white-back border-brown-dark flex items-center justify-between rounded-lg border py-5 pr-12 pl-7 max-md:flex-col max-md:items-start max-md:px-5 max-md:py-4">
      <div className="flex items-center gap-7 max-md:gap-4">
        <label
          htmlFor={`trashItem-${uid}`}
          className="flex cursor-pointer items-center gap-5 max-md:gap-3"
        >
          <input
            type="checkbox"
            id={`trashItem-${uid}`}
            className="accent-accent-pink h-4.5 w-4.5 max-md:h-4 max-md:w-4"
          />
          <Image
            src={trashItem.media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
            alt=""
            className="aspect-square rounded-lg object-cover max-md:h-20 max-md:w-20"
            width={140}
            height={140}
          />
        </label>
        <div>
          <p className="max-md:text-xs">{trashItem.media.originalFilename}</p>
          <p className="mt-1 text-[13px] max-md:text-[11px]">
            {Number(sizeInKB) >= 1024 ? `${sizeInMB}MB` : `${sizeInKB}KB`} {trashItem.media.width} ×{" "}
            {trashItem.media.height}
          </p>
          <p className="mt-1 text-[13px] max-md:text-[11px]">
            削除日：{formatJapaneseDateNonTime(trashItem.expiresAt)}
          </p>
          <p className="text-warning mt-1 text-[13px] max-md:text-[11px]">
            あと{calculateRemainingDays(trashItem.expiresAt)}日
          </p>
        </div>
      </div>
      <div className="flex gap-5 max-lg:flex-col max-md:mt-5 max-md:flex-row max-md:gap-4">
        <Button variant="cancel">復元する</Button>
        <Button variant="remove">完全に削除する</Button>
      </div>
    </div>
  );
};
