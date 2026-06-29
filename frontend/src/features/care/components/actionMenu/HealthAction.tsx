"use client";

import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { useState } from "react";

import { toast } from "@/components/ui/Toast";

import { createCareRecordAction } from "../../actions/createCareRecordAction";
import healthIcon from "../../assets/health.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const HealthAction = () => {
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
  const [temperature, setTemperature] = useState<number>(36.5);

  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // 登録処理
  const saveAction = () => {
    const recordTime = new Date(date + " " + time);

    if (!(recordTime instanceof Date) || Number.isNaN(recordTime.getTime())) {
      toast.error("無効な日時です");
      return;
    }

    if (temperature < 34 || temperature > 42) {
      toast.error("体温を正しく入力してください");
      return;
    }

    startTransition(async () => {
      const result = await createCareRecordAction({
        recordType: "HEALTH",
        recordedAt: recordTime,
        healthDetail: { temperature, note },
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["careRecords"] });
        setIsOpen(false);
        setNote("");
        toast.success("体調を記録しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="max-md:w-[calc(50%-8px)]">
      <CareActionModal
        title="体調"
        isOpen={isOpen}
        icon={healthIcon}
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
        <div className="my-4 flex items-end gap-2 max-md:justify-center">
          <span className="">体温</span>
          <input
            type="number"
            min={34}
            max={42}
            step={0.1}
            className="border-line-gray h-10 rounded-sm border bg-white pl-2 text-2xl font-medium max-md:text-xl"
            value={temperature}
            onChange={(e) => setTemperature(Number(e.target.value))}
            disabled={isPending}
          />
          <span className="">℃</span>
        </div>
      </CareActionModal>
      <button
        className="border-health-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
        disabled={isPending}
      >
        <Image
          src={healthIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-health-text mt-3 text-lg font-medium max-md:text-[16px]">体調</p>
      </button>
    </div>
  );
};
