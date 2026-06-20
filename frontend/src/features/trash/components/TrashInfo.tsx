import Image from "next/image";

import { TrashItemListResponseDto } from "@/lib/api-client/gen";

import timerIcon from "../assets/timer.svg";

type Props = {
  trashItems: TrashItemListResponseDto;
};
export const TrashInfo = ({ trashItems }: Props) => {
  return (
    <>
      <p className="mt-7.5 max-md:text-[13px]">
        削除したアイテムは30日間保持されます。
        <br className="md:hidden" />
        期限を過ぎると自動的に完全削除されます。
      </p>
      <div className="bg-brown-back border-brown-middle mt-7.5 flex items-center gap-4 rounded-lg border p-5 max-md:mt-6 max-md:items-start max-md:gap-3 max-md:p-3">
        <Image src={timerIcon} alt="" className="shrink-0" width={30} height={30} />
        <p className="text-brown-middle text-sm max-md:text-[13px]">
          <span className="text-lg font-medium max-md:text-[15px]">{trashItems.totalCount}件</span>
          のアイテムがゴミ箱にあります。
          {trashItems.earliest && (
            <>
              <br className="md:hidden" />
              最も期限が近いアイテムはあと
              <span className="text-lg font-medium max-md:text-[15px]">
                {trashItems.earliest}日
              </span>
              で完全削除されます。
            </>
          )}
        </p>
      </div>
    </>
  );
};
