import Image from "next/image";

import icon from "../assets/calender-icon.svg";

export const DateRangeFilter = () => {
  return (
    <div className="bg-brown-back shrink-0 rounded-lg px-7 pt-6 pb-8 max-md:p-3 max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="max-md:text-[13px]">期間</p>
      </div>
      <div className="mt-3 flex items-center gap-2">
        <input
          type="date"
          className="focus:outline-brown-light border-line-gray h-9 w-35 rounded-sm border bg-white px-3 text-sm max-md:h-9 max-md:w-[calc(50%-15px)] max-md:text-xs"
        />
        <p className="max-md:text-[13px]">〜</p>
        <input
          type="date"
          className="focus:outline-brown-light border-line-gray h-9 w-35 rounded-sm border bg-white px-3 text-sm max-md:h-9 max-md:w-[calc(50%-15px)] max-md:text-xs"
        />
      </div>
    </div>
  );
};
