"use client";

import Image from "next/image";
import { Dispatch, SetStateAction } from "react";

import { CareRecordResponseDto, GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";
import { formatJapaneseDateTimeOnly } from "@/utils/date";

import growthIcon from "../../assets/growth.svg";
import arrow from "../../assets/popArrow.svg";
import { useCalendarPop } from "../../hooks/useCalendarPop";

type Props = {
  state: {
    isPopOpen: boolean;
    top: number;
    left: number;
    record: CareRecordResponseDto | null;
    growthRecord: GrowthRecordResponseDto | null;
    weekIndex: number;
  };
  popCloseAction: () => void;
  setIsDeleteConfirmOpen: Dispatch<SetStateAction<boolean>>;
};
export const CalendarPop = ({ state, popCloseAction, setIsDeleteConfirmOpen }: Props) => {
  const {
    uid,
    dataMap,
    isEditMode,
    setIsEditMode,
    isPending,
    updateData,
    setUpdateData,
    popRef,
    editModeOpen,
    saveCareRecordAction,
    saveGrowthRecordAction,
  } = useCalendarPop({
    state,
    popCloseAction,
  });

  let translateClass = "";
  switch (state.weekIndex) {
    case 6:
      translateClass = "@md:-translate-x-[220px]";
      break;
    case 5:
      translateClass = "@md:-translate-x-[120px]";
      break;
    default:
      break;
  }

  return (
    <>
      {state.isPopOpen && (state.record || state.growthRecord) && (
        <div
          className="absolute z-50 -translate-y-[calc(100%+15px)] @max-md:static @max-md:translate-y-0"
          ref={popRef}
          style={{
            top: state.top,
            left: state.left - 22,
          }}
        >
          <div
            className={cn(
              "border-accent-pink bg-background-normal/80 w-fit min-w-80 rounded-lg border backdrop-blur-[30px] @max-md:absolute @max-md:-left-2.5 @max-md:w-[calc(100vw-20px)] @max-md:-translate-y-[calc(100%+15px)]",
              translateClass,
            )}
            style={{ top: state.top }}
          >
            <div className="relative">
              {/* 閉じるボタン */}
              <button
                className="absolute top-2 right-2 cursor-pointer transition-all hover:opacity-70"
                onClick={popCloseAction}
                disabled={isPending}
              >
                <Image src="/images/modal-close.svg" alt="" width={10} height={10} />
              </button>
              <div className="flex gap-3 px-6 py-3">
                {/* アイコンと種別 */}
                <div className="shrink-0 text-center">
                  <Image
                    src={state.record ? dataMap[state.record.recordType].icon : growthIcon}
                    alt=""
                    className="mx-auto"
                    width={55}
                    height={55}
                  />
                  <p
                    className={cn(
                      "mt-2 text-[13px] font-medium",
                      state.record ? dataMap[state.record.recordType].color : "text-growth-text",
                    )}
                  >
                    {state.record ? dataMap[state.record.recordType].label : "身長・体重"}
                  </p>
                </div>
                <div className="flex w-full flex-col justify-between">
                  <div>
                    {/* 表示モード時 */}
                    {!isEditMode && (
                      <>
                        {state.record && (
                          <>
                            <div className="flex gap-3">
                              {/* 日時 */}
                              <p className="text-sm font-medium">
                                {formatJapaneseDateTimeOnly(new Date(state.record.recordedAt))}
                              </p>
                              {/* ミルク量表示 */}
                              {state.record.milkDetail && (
                                <p className="text-sm font-medium">
                                  {state.record.milkDetail.amountMl}ml
                                </p>
                              )}
                              {/* 排泄タイプ表示 */}
                              {state.record.diaperDetail && (
                                <p className="text-sm font-medium">
                                  {state.record.diaperDetail.diaperType === "WET" ? "おしっこ" : ""}
                                  {state.record.diaperDetail.diaperType === "DIRTY" ? "うんち" : ""}
                                </p>
                              )}
                              {/* 体温表示 */}
                              {state.record.healthDetail && (
                                <p className="text-sm font-medium">
                                  {state.record.healthDetail.temperature}℃
                                </p>
                              )}
                            </div>
                            {/* メモ表示 */}
                            <p className="mt-2 text-[13px]">
                              {state.record.mealDetail?.note ||
                                state.record.milkDetail?.note ||
                                state.record.diaperDetail?.note ||
                                state.record.healthDetail?.note}
                            </p>
                          </>
                        )}
                        {state.growthRecord && (
                          <>
                            {/* 身長と体重表示 */}
                            {state.growthRecord.height && (
                              <p className="text-sm font-medium">
                                身長: {state.growthRecord.height}cm
                              </p>
                            )}
                            {state.growthRecord.weight && (
                              <p className="text-sm font-medium">
                                体重: {state.growthRecord.weight}kg
                              </p>
                            )}
                            {/* メモ表示 */}
                            <p className="mt-2 text-[13px]">{state.growthRecord.note}</p>
                          </>
                        )}
                      </>
                    )}
                    {/* 編集モード時 */}
                    {isEditMode && (
                      <>
                        {/* 日時編集 */}
                        <div className="mb-2 flex gap-4">
                          <input
                            type="date"
                            className="text-xs outline-none"
                            value={updateData.date}
                            onChange={(e) => {
                              setUpdateData((prev) => ({
                                ...prev,
                                date: e.target.value,
                              }));
                            }}
                            disabled={isPending}
                          />
                          {/* 身長・体重のときは時間を編集しない */}
                          {!state.growthRecord && (
                            <input
                              type="time"
                              className="text-xs outline-none"
                              value={updateData.time}
                              onChange={(e) => {
                                setUpdateData((prev) => ({
                                  ...prev,
                                  time: e.target.value,
                                }));
                              }}
                              disabled={isPending}
                            />
                          )}
                        </div>
                        {state.record && (
                          <>
                            {/* ミルク量編集 */}
                            {state.record.milkDetail && (
                              <>
                                <input
                                  type="number"
                                  value={Number(updateData.amountMl)}
                                  min={10}
                                  max={400}
                                  step={10}
                                  className="border-line-gray bg-light-dark rounded-sm border px-1 text-sm font-medium outline-0"
                                  onChange={(e) => {
                                    setUpdateData((prev) => ({
                                      ...prev,
                                      amountMl: Number(e.target.value),
                                    }));
                                  }}
                                />
                                <span className="ml-2 text-sm">ml</span>
                              </>
                            )}
                            {/* 排泄タイプ編集 */}
                            {state.record.diaperDetail && (
                              <div className="flex gap-2">
                                <label
                                  htmlFor={`diaper-wet-${uid}`}
                                  className="accent-accent-pink flex items-center gap-1 text-xs font-medium"
                                >
                                  <input
                                    type="radio"
                                    name={`diaper-${uid}`}
                                    id={`diaper-wet-${uid}`}
                                    checked={updateData.diaperType === "WET"}
                                    onChange={() => {
                                      setUpdateData((prev) => ({
                                        ...prev,
                                        diaperType: "WET",
                                      }));
                                    }}
                                  />
                                  <span>おしっこ</span>
                                </label>
                                <label
                                  htmlFor={`diaper-dirty-${uid}`}
                                  className="accent-accent-pink flex items-center gap-1 text-xs font-medium"
                                >
                                  <input
                                    type="radio"
                                    name={`diaper-${uid}`}
                                    id={`diaper-dirty-${uid}`}
                                    checked={updateData.diaperType === "DIRTY"}
                                    onChange={() => {
                                      setUpdateData((prev) => ({
                                        ...prev,
                                        diaperType: "DIRTY",
                                      }));
                                    }}
                                  />
                                  <span>うんち</span>
                                </label>
                              </div>
                            )}
                            {/* 体温編集 */}
                            {state.record.healthDetail && (
                              <>
                                <input
                                  type="number"
                                  value={Number(updateData.temperature)}
                                  min={34}
                                  max={42}
                                  step={0.1}
                                  className="border-line-gray bg-light-dark rounded-sm border px-1 text-sm font-medium outline-0"
                                  onChange={(e) => {
                                    setUpdateData((prev) => ({
                                      ...prev,
                                      temperature: Number(e.target.value),
                                    }));
                                  }}
                                />
                                <span className="ml-2 text-sm">℃</span>
                              </>
                            )}
                          </>
                        )}

                        {/* 成長記録編集 */}
                        {state.growthRecord && (
                          <>
                            <div className="flex gap-1">
                              <p className="text-sm font-medium">身長: </p>
                              <input
                                type="number"
                                min={0}
                                max={200}
                                step={0.1}
                                className="border-line-gray bg-light-dark w-20 rounded-sm border px-1.5 text-sm font-medium outline-none"
                                value={updateData.height ?? ""}
                                onChange={(e) => {
                                  setUpdateData((prev) => ({
                                    ...prev,
                                    height:
                                      e.target.value === "" ? undefined : Number(e.target.value),
                                  }));
                                }}
                                disabled={isPending}
                              />
                              <p className="text-sm font-medium">cm</p>
                            </div>

                            <div className="mt-1 flex gap-1">
                              <p className="text-sm font-medium">体重: </p>
                              <input
                                type="number"
                                min={0}
                                max={200}
                                step={0.1}
                                className="border-line-gray bg-light-dark w-20 rounded-sm border px-1.5 text-sm font-medium outline-none"
                                value={updateData.weight ?? ""}
                                onChange={(e) => {
                                  setUpdateData((prev) => ({
                                    ...prev,
                                    weight:
                                      e.target.value === "" ? undefined : Number(e.target.value),
                                  }));
                                }}
                                disabled={isPending}
                              />
                              <p className="text-sm font-medium">kg</p>
                            </div>
                          </>
                        )}
                        {/* メモ編集 */}
                        <input
                          type="text"
                          className="border-line-gray bg-light-dark mt-2 w-full rounded-sm border p-1 text-xs outline-0"
                          value={updateData.note}
                          onChange={(e) => {
                            setUpdateData((prev) => ({
                              ...prev,
                              note: e.target.value,
                            }));
                          }}
                          disabled={isPending}
                        />
                      </>
                    )}
                  </div>
                  <div className="mt-6 ml-auto flex w-fit gap-3">
                    {/* 表示モード時のボタン */}
                    {!isEditMode && (
                      <>
                        <button
                          className="cursor-pointer text-xs underline transition-all hover:opacity-70"
                          onClick={editModeOpen}
                          disabled={isPending}
                        >
                          編集
                        </button>
                        <button
                          className="text-warning cursor-pointer text-xs underline transition-all hover:opacity-70 dark:font-medium"
                          onClick={() => setIsDeleteConfirmOpen(true)}
                          disabled={isPending}
                        >
                          削除
                        </button>
                      </>
                    )}
                    {/* 編集モード時のボタン */}
                    {isEditMode && (
                      <>
                        <button
                          className="cursor-pointer text-xs underline transition-all hover:opacity-70"
                          onClick={() => setIsEditMode(false)}
                          disabled={isPending}
                        >
                          戻る
                        </button>
                        {state.record && (
                          <button
                            className="text-success cursor-pointer text-xs underline transition-all hover:opacity-70"
                            onClick={saveCareRecordAction}
                            disabled={isPending}
                          >
                            更新
                          </button>
                        )}
                        {state.growthRecord && (
                          <button
                            className="text-success cursor-pointer text-xs underline transition-all hover:opacity-70"
                            onClick={saveGrowthRecordAction}
                            disabled={isPending}
                          >
                            更新
                          </button>
                        )}
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
          {/* 吹き出しの矢印 */}
          <Image
            src={arrow}
            alt=""
            className="absolute -bottom-1.5 left-6 @max-md:hidden"
            width={25}
            height={8}
          />
          <Image
            src={arrow}
            alt=""
            className="absolute -bottom-1.5 left-6 @md:hidden"
            width={25}
            height={8}
            style={{
              top: state.top - 16,
              left: state.left + 3,
            }}
          />
        </div>
      )}
    </>
  );
};
