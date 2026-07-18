"use client";

import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { useState } from "react";

import { toast } from "@/components/ui/Toast";
import { DiaperDetailDtoDiaperTypeEnum } from "@/lib/api-client/gen";

import { createCareRecordAction } from "../../actions/createCareRecordAction";
import diaperIcon from "../../assets/diaper.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const DiaperAction = () => {
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
  const [diaperType, setDiaperType] = useState<DiaperDetailDtoDiaperTypeEnum>("WET");
  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // 登録処理
  const saveAction = () => {
    const recordTime = new Date(date + " " + time);

    if (!(recordTime instanceof Date) || Number.isNaN(recordTime.getTime())) {
      toast.error("無効な日時です");
      return;
    }

    startTransition(async () => {
      const result = await createCareRecordAction({
        recordType: "DIAPER",
        recordedAt: recordTime,
        diaperDetail: { diaperType, note },
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["careRecords"] });
        setIsOpen(false);
        setNote("");
        toast.success("排泄を記録しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="@max-md:w-[calc(50%-8px)]">
      <CareActionModal
        title="排泄"
        isOpen={isOpen}
        icon={diaperIcon}
        date={date}
        setDate={setDate}
        time={time}
        setTime={setTime}
        onCancel={() => setIsOpen(false)}
        note={note}
        setNote={setNote}
        saveAction={saveAction}
        isPending={isPending}
      >
        <div className="my-4 flex gap-4">
          <label htmlFor="WET" className="flex cursor-pointer items-center gap-2">
            <input
              type="radio"
              id="WET"
              className="accent-accent-pink h-5 w-5"
              checked={diaperType === "WET"}
              onChange={() => setDiaperType("WET")}
            />
            <p className="text-xl font-medium">おしっこ</p>
          </label>
          <label htmlFor="DIRTY" className="flex cursor-pointer items-center gap-2">
            <input
              type="radio"
              id="DIRTY"
              className="accent-accent-pink h-5 w-5"
              checked={diaperType === "DIRTY"}
              onChange={() => setDiaperType("DIRTY")}
            />
            <p className="text-xl font-medium">うんち</p>
          </label>
        </div>
      </CareActionModal>

      <button
        type="button"
        className="border-diaper-border group bg-translucent w-full cursor-pointer rounded-lg border p-5 backdrop-blur-[7.5px] transition-all @max-md:rounded-4xl @max-md:p-4"
        disabled={isPending}
        onClick={openModal}
      >
        <Image
          src={diaperIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 @max-md:h-20 @max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-diaper-text mt-3 text-lg font-medium @max-md:text-[16px]">排泄</p>
      </button>
    </div>
  );
};
