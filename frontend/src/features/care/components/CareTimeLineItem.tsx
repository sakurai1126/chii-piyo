import Image from "next/image";

import { CareRecordResponseDto, GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateTimeOnly } from "@/utils/date";

import diaperIcon from "../assets/diaper.svg";
import growthIcon from "../assets/growth.svg";
import healthIcon from "../assets/health.svg";
import mealIcon from "../assets/meal.svg";
import milkIcon from "../assets/milk.svg";

type Props =
  | { index: number; careItem: CareRecordResponseDto; growthItem?: never }
  | { index: number; growthItem: GrowthRecordResponseDto; careItem?: never };

export const CareTimeLineItem = ({ index, careItem, growthItem }: Props) => {
  const dataMap = {
    MEAL: {
      icon: mealIcon,
      label: "食事",
    },
    MILK: {
      icon: milkIcon,
      label: "ミルク",
    },
    DIAPER: {
      icon: diaperIcon,
      label: "排泄",
    },
    HEALTH: {
      icon: healthIcon,
      label: "体調",
    },
  };

  return (
    <>
      <div className="relative flex items-center gap-10 py-2.5 max-md:gap-6 max-md:py-2">
        <div
          className={`bg-brown-dark h-2 w-2 shrink-0 rounded-full ${growthItem ? "opacity-0" : ""}`}
        ></div>
        <div
          className={`bg-brown-dark absolute left-1 h-full w-px ${index === 0 ? "top-[50%]" : ""} ${growthItem ? "opacity-0" : ""}`}
        ></div>

        <div className="bg-white-back border-brown-dark w-full gap-5 rounded-lg border px-6 py-3 max-md:p-3">
          <div className="flex items-start gap-5">
            {careItem && (
              <Image
                src={dataMap[careItem.recordType].icon}
                alt=""
                className="max-md:h-12 max-md:w-12"
                width={60}
                height={60}
              />
            )}
            {growthItem && (
              <Image
                src={growthIcon}
                alt=""
                className="max-md:h-12 max-md:w-12"
                width={60}
                height={60}
              />
            )}

            <div>
              {growthItem && (
                <>
                  {growthItem.height && (
                    <p className="font-medium max-md:text-sm">身長: {growthItem.height}cm</p>
                  )}
                  {growthItem.weight && (
                    <p className="font-medium max-md:text-sm">体重: {growthItem.weight}kg</p>
                  )}
                </>
              )}

              {careItem && (
                <p className="text-xl font-medium max-md:text-sm">
                  {formatJapaneseDateTimeOnly(careItem.recordedAt)}
                </p>
              )}

              <div className="mt-1 flex items-center gap-3">
                {careItem && <p className="max-md:text-xs">{dataMap[careItem.recordType].label}</p>}

                {careItem?.milkDetail && (
                  <>
                    <span className="bg-line-gray h-px w-8 max-md:w-4"></span>
                    <p className="max-md:text-xs">{careItem?.milkDetail.amountMl}ml</p>
                  </>
                )}
                {careItem?.diaperDetail && (
                  <>
                    <span className="bg-line-gray h-px w-8 max-md:w-4"></span>
                    <p className="max-md:text-xs">
                      {careItem?.diaperDetail.diaperType === "WET" ? "おしっこ" : ""}
                      {careItem?.diaperDetail.diaperType === "DIRTY" ? "うんち" : ""}
                    </p>
                  </>
                )}
                {careItem?.healthDetail && (
                  <>
                    <span className="bg-line-gray h-px w-8 max-md:w-4"></span>
                    <p className="max-md:text-xs">体温: {careItem?.healthDetail.temperature}°C</p>
                  </>
                )}
              </div>
              <p className="mt-2 max-md:mt-1 max-md:text-xs">
                {careItem?.mealDetail?.note ||
                  careItem?.milkDetail?.note ||
                  careItem?.diaperDetail?.note ||
                  careItem?.healthDetail?.note ||
                  growthItem?.note}
              </p>
            </div>
          </div>
          <div className="mt-auto ml-auto flex w-fit shrink-0 gap-5 max-md:mt-1 max-md:gap-3">
            <button className="text-sm underline max-md:text-[10px]">編集</button>
            <button className="text-warning text-sm underline max-md:text-[10px]">削除</button>
          </div>
        </div>
      </div>
    </>
  );
};
