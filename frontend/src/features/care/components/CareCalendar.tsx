"use client";

import Image from "next/image";
import Link from "next/link";

import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { CareRecordListResponseDto, GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateBasic } from "@/utils/date";

import diaperIcon from "../assets/diaper.svg";
import growthIcon from "../assets/growth.svg";
import healthIcon from "../assets/health.svg";
import mealIcon from "../assets/meal.svg";
import milkIcon from "../assets/milk.svg";
import piyoIcon from "../assets/piyo.svg";
import plusIcon from "../assets/plus.svg";
import { useCalendar } from "../hooks/useCalendar";

import { CalendarPop } from "./ui/CalendarPop";

type Props = {
  initialCareRecords: CareRecordListResponseDto;
  initialGrowthRecords: GrowthRecordResponseDto[];
};
export const CareCalendar = ({ initialCareRecords, initialGrowthRecords }: Props) => {
  const {
    weeklyText,
    today,
    startDay,
    currentDay,
    isTodayWeek,
    weeklyDates,
    changeWeek,
    changeDays,
    careRecords,
    growthRecords,
    pop,
    popCloseAction,
    isPending,
    isDeleteConfirmOpen,
    setIsDeleteConfirmOpen,
    deleteAction,
    growthItemTapAction,
    careItemTapAction,
  } = useCalendar({ initialCareRecords, initialGrowthRecords });

  const iconMap = {
    MEAL: mealIcon,
    MILK: milkIcon,
    DIAPER: diaperIcon,
    HEALTH: healthIcon,
  };

  return (
    <>
      {/* 年月表示 */}
      <div className="mt-20 flex items-center justify-center gap-10 max-md:mt-10 max-md:justify-between max-md:gap-0">
        <button
          className="cursor-pointer pt-1 text-sm transition-all hover:opacity-70 max-md:text-xs"
          onClick={() => changeWeek(-7)}
        >
          &lt; 前の週
        </button>
        <div className="flex items-center">
          <span className="text-[26px] font-medium max-md:text-xl">{startDay.getFullYear()}</span>
          <span className="ml-1 pt-1 text-lg max-md:text-sm">年</span>
          <span className="ml-3 text-[26px] font-medium max-md:text-xl">
            {startDay.getMonth() + 1}
          </span>
          <span className="ml-1 pt-1 text-lg max-md:text-sm">月</span>
        </div>
        <button
          className="cursor-pointer pt-1 text-sm transition-all hover:opacity-70 max-md:text-xs"
          onClick={() => changeWeek(7)}
        >
          次の週 &gt;
        </button>
      </div>
      {/* カレンダー表示 */}
      <div className="border-brown-dark relative mt-4 w-full rounded-xl border-2 bg-white/50 backdrop-blur-[7.5px] max-md:h-auto">
        {pop.isPopOpen && (
          <CalendarPop
            state={pop}
            popCloseAction={popCloseAction}
            setIsDeleteConfirmOpen={setIsDeleteConfirmOpen}
          />
        )}

        {/* 日付・曜日表示行 */}
        <div className="bg-calender-head border-line-gray flex h-10 rounded-t-xl border-b">
          <span className="w-10 shrink-0"></span>
          <div className="grid w-full grid-cols-7 max-md:grid-cols-1">
            {Array.from({ length: 7 }, (_, index) => (
              <div
                className={`border-brown-dark/50 flex h-10 items-center justify-between border-l px-5 max-lg:justify-center max-lg:px-0 max-md:justify-between max-md:px-5 ${index === currentDay.getDay() ? "" : "max-md:hidden"} ${isTodayWeek && index === today.getDay() ? "md:bg-brown-middle md:font-medium md:text-white" : ""}`}
                key={weeklyDates[index].toISOString()}
              >
                {/* 前日へ移動(モバイルのみ) */}
                <button
                  className="text-brown-dark text-xs outline-0 md:hidden"
                  onClick={() => changeDays(-1)}
                >
                  &lt;
                </button>
                <div className="flex items-center gap-2">
                  {/* 日付・曜日表示 */}
                  <p className="text-sm max-lg:text-xs">
                    {weeklyDates[index].getMonth() + 1}月{weeklyDates[index].getDate()}(
                    {weeklyText[index]})
                  </p>
                  {/* タイムライン表示アイコン */}
                  <Link
                    href={`/care/${formatJapaneseDateBasic(weeklyDates[index])}`}
                    scroll={false}
                    className="cursor-pointer transition-all hover:opacity-70"
                  >
                    <Image src={plusIcon} alt="" width={12} height={12} />
                  </Link>
                </div>
                {/* 翌日へ移動(モバイルのみ) */}
                <button
                  className="text-brown-dark text-xs outline-0 md:hidden"
                  onClick={() => changeDays(1)}
                >
                  &gt;
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* 身長・体重アイコン表示行 */}
        <div className="flex h-10">
          <div className="bg-calender-head/50 grid h-10 w-10 shrink-0 place-content-center">
            <Image src={piyoIcon} alt="" className="" width={15} height={15} />
          </div>
          <div className="grid w-full grid-cols-7 max-md:grid-cols-1">
            {Array.from({ length: 7 }, (_, dayIndex) => (
              <div
                className={`border-brown-dark/50 flex h-10 items-center gap-1 overflow-scroll border-l px-2 ${dayIndex === currentDay.getDay() ? "" : "max-md:hidden"}`}
                key={weeklyDates[dayIndex].toISOString()}
              >
                {growthRecords
                  ?.filter((item) => {
                    // 記録日時をDate型に変換し対象日時と比較
                    const recordDate = new Date(item.measurementDate);
                    const targetDate = weeklyDates[dayIndex];
                    return (
                      recordDate.getFullYear() === targetDate.getFullYear() &&
                      recordDate.getMonth() === targetDate.getMonth() &&
                      recordDate.getDate() === targetDate.getDate()
                    );
                  })
                  .map((item) => (
                    <button
                      key={item.id}
                      className="border-accent-pink cursor-pointer rounded-full border"
                      onClick={growthItemTapAction(item, dayIndex)}
                    >
                      <Image src={growthIcon} alt="" width={30} height={30} />
                    </button>
                  ))}
              </div>
            ))}
          </div>
        </div>

        {/* 育児記録アイコン表示行（24時間） */}
        {Array.from({ length: 24 }, (_, timeIndex) => (
          <div
            key={`hour-${timeIndex}`}
            className="border-line-gray flex h-10 border-t border-dashed"
          >
            <p className="grid h-10 w-10 shrink-0 place-content-center text-sm">{timeIndex}</p>
            <div className="grid w-full grid-cols-7 max-md:grid-cols-1">
              {Array.from({ length: 7 }, (_, dayIndex) => (
                <div
                  className={`border-brown-dark/50 flex h-10 items-center gap-1 overflow-scroll border-l px-2 ${dayIndex === currentDay.getDay() ? "" : "max-md:hidden"}`}
                  key={weeklyDates[dayIndex].toISOString()}
                >
                  {careRecords?.items
                    .filter((item) => {
                      // 記録日時をDate型に変換し対象日時と比較
                      const recordDate = new Date(item.recordedAt);
                      const targetDate = weeklyDates[dayIndex];
                      return (
                        recordDate.getFullYear() === targetDate.getFullYear() &&
                        recordDate.getMonth() === targetDate.getMonth() &&
                        recordDate.getDate() === targetDate.getDate() &&
                        recordDate.getHours() === timeIndex
                      );
                    })
                    .map((item) => (
                      <button
                        key={item.id}
                        className="border-accent-pink cursor-pointer rounded-full border"
                        onClick={careItemTapAction(item, dayIndex)}
                      >
                        <Image src={iconMap[item.recordType]} alt="" width={30} height={30} />
                      </button>
                    ))}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* 削除確認モーダル */}
      <ConfirmModal
        isOpen={isDeleteConfirmOpen}
        isPending={isPending}
        action={deleteAction}
        closeAction={() => setIsDeleteConfirmOpen(false)}
        message="選択した記録を削除します。"
        buttonType="remove"
        buttonMessage="削除する"
      />
    </>
  );
};
