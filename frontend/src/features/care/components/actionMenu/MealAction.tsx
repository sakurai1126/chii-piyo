"use client";

import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";

import { toast } from "@/components/ui/Toast";

import { createCareRecordAction } from "../../actions/createCareRecordAction";
import mealIcon from "../../assets/meal.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const MealAction = () => {
  const {
    isOpen,
    setIsOpen,
    isPending,
    startTransition,
    note,
    setNote,
    date,
    setDate,
    time,
    setTime,
    openModal,
  } = useCareRecord();

  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // 登録処理
  const saveAction = () => {
    const recordTime = new Date(date + " " + time);

    if (!(recordTime instanceof Date) || Number.isNaN(recordTime.getTime())) {
      toast.error("無効な日時です");
      return;
    }

    // 登録処理
    startTransition(async () => {
      const result = await createCareRecordAction({
        recordType: "MEAL",
        recordedAt: recordTime,
        mealDetail: { note },
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["careRecords"] });
        setIsOpen(false);
        setNote("");
        toast.success("食事を記録しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="max-md:w-[calc(50%-8px)]">
      <CareActionModal
        title="食事"
        isOpen={isOpen}
        icon={mealIcon}
        date={date}
        setDate={setDate}
        time={time}
        setTime={setTime}
        onCancel={() => setIsOpen(false)}
        note={note}
        setNote={setNote}
        saveAction={saveAction}
        isPending={isPending}
      />

      <button
        className="border-meal-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
        disabled={isPending}
      >
        <Image
          src={mealIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-meal-text mt-3 text-lg font-medium max-md:text-[16px]">食事</p>
      </button>
    </div>
  );
};
