"use client";
import Image from "next/image";
import { useState } from "react";

import mealIcon from "../assets/meal.svg";
import plusIcon from "../assets/plus.svg";

export const CareCalendar = () => {
  const weeklyText = ["日", "月", "火", "水", "木", "金", "土"];
  const [currentDays] = useState<number>(0);

  return (
    <>
      {/* 年月表示 */}
      <div className="mt-20 flex items-center justify-center gap-10 max-md:mt-10 max-md:justify-between max-md:gap-0">
        <button className="pt-1 text-sm max-md:text-xs">&lt; 前の週</button>
        <div className="flex items-center">
          <span className="text-[26px] font-medium max-md:text-xl">2026</span>
          <span className="ml-1 pt-1 text-lg max-md:text-sm">年</span>
          <span className="ml-4 text-[26px] font-medium max-md:text-xl">1</span>
          <span className="ml-1 pt-1 text-lg max-md:text-sm">月</span>
          <span className="ml-4 text-[26px] font-medium max-md:text-xl">1</span>
          <span className="ml-1 pt-1 text-lg max-md:text-sm">週目</span>
        </div>
        <button className="pt-1 text-sm max-md:text-xs">次の週 &gt;</button>
      </div>
      {/* カレンダー表示 */}
      <div className="border-brown-dark relative mt-4 h-150 w-full overflow-y-scroll rounded-xl border-2 bg-white/50 backdrop-blur-[7.5px] max-md:h-auto">
        {/* 曜日 */}
        <div className="bg-calender-head border-line-gray sticky top-0 flex h-10 border-b">
          <span className="w-10 shrink-0"></span>
          <div className="grid w-full grid-cols-7 max-md:grid-cols-1">
            {Array.from({ length: 7 }, (_, index) => (
              <div
                className={`border-brown-dark/50 flex h-10 items-center justify-center gap-2 border-l ${index !== currentDays ? "max-md:hidden" : ""}`}
                key={index}
              >
                <p className="text-sm max-lg:text-xs">1月1日({weeklyText[index]})</p>
                <button className="cursor-pointer transition-all hover:opacity-70">
                  <Image src={plusIcon} alt="" width={12} height={12} />
                </button>
              </div>
            ))}
          </div>
        </div>

        {Array.from({ length: 24 }, (_, timeIndex) => (
          <div
            key={timeIndex}
            className={`flex h-10 ${timeIndex !== 0 ? "border-line-gray border-t border-dashed" : ""}`}
          >
            <p className="grid h-10 w-10 shrink-0 place-content-center text-sm">{timeIndex}</p>
            <div className="grid w-full grid-cols-7 max-md:grid-cols-1">
              {Array.from({ length: 7 }, (_, dayIndex) => (
                <div
                  className={`border-brown-dark/50 flex h-10 items-center gap-1 overflow-scroll border-l px-2 ${dayIndex !== currentDays ? "max-md:hidden" : ""}`}
                  key={dayIndex}
                >
                  <button className="border-accent-pink cursor-pointer rounded-full border">
                    <Image src={mealIcon} alt="" width={30} height={30} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </>
  );
};
