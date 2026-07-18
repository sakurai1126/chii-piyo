"use client";

import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { useState } from "react";

import { toast } from "@/components/ui/Toast";

import { createGrowthRecordAction } from "../../actions/createGrowthRecordAction";
import growthIcon from "../../assets/growth.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const GrowthAction = () => {
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

  const [height, setHeight] = useState<number | undefined>(undefined);
  const [weight, setWeight] = useState<number | undefined>(undefined);
  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // 登録処理
  const saveAction = () => {
    const recordTime = new Date(date + " " + time);

    if (!(recordTime instanceof Date) || Number.isNaN(recordTime.getTime())) {
      toast.error("無効な日時です");
      return;
    }

    if (!height && !weight) {
      toast.error("身長または体重のいずれかは必ず入力してください");
      return;
    }

    if ((height && height <= 0) || (height && height > 200)) {
      toast.error("身長を正しく入力してください");
      return;
    }

    if ((weight && weight <= 0) || (weight && weight > 200)) {
      toast.error("体重を正しく入力してください");
      return;
    }

    startTransition(async () => {
      const result = await createGrowthRecordAction({
        measurementDate: recordTime,
        height: height || undefined,
        weight: weight || undefined,
        note,
      });

      if (result.success) {
        queryClient.invalidateQueries({ queryKey: ["growthRecords"] });
        setIsOpen(false);
        setWeight(undefined);
        setHeight(undefined);
        setNote("");
        toast.success("身長/体重を記録しました");
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="@max-md:w-full">
      <CareActionModal
        title="身長/体重"
        isOpen={isOpen}
        icon={growthIcon}
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
        <div className="my-4 flex gap-6 @max-md:justify-center @max-md:gap-3">
          <div className="flex items-end gap-2">
            <span className="@max-md:text-sm">身長</span>
            <input
              type="number"
              min={0}
              max={200}
              step={0.1}
              className="border-line-gray bg-light-dark h-10 rounded-sm border pl-2 text-2xl font-medium @max-md:pr-1 @max-md:text-xl"
              disabled={isPending}
              onChange={(e) =>
                setHeight(e.target.value === "" ? undefined : Number(e.target.value))
              }
              value={height ?? ""}
            />
            <span className="@max-md:text-sm">cm</span>
          </div>
          <div className="flex items-end gap-2">
            <span className="@max-md:text-sm">体重</span>
            <input
              type="number"
              min={0}
              max={200}
              step={0.1}
              className="border-line-gray bg-light-dark h-10 rounded-sm border pl-2 text-2xl font-medium @max-md:pr-1 @max-md:text-xl"
              disabled={isPending}
              onChange={(e) =>
                setWeight(e.target.value === "" ? undefined : Number(e.target.value))
              }
              value={weight ?? ""}
            />
            <span className="@max-md:text-sm">kg</span>
          </div>
        </div>
      </CareActionModal>

      <button
        type="button"
        className="border-growth-border group bg-translucent w-full cursor-pointer rounded-lg border p-5 backdrop-blur-[7.5px] transition-all @max-md:flex @max-md:items-center @max-md:justify-center @max-md:gap-3 @max-md:rounded-4xl @max-md:p-4"
        onClick={openModal}
        disabled={isPending}
      >
        <Image
          src={growthIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 @max-md:m-0 @max-md:h-20 @max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-growth-text mt-3 text-lg font-medium @max-md:text-[16px]">身長/体重</p>
      </button>
    </div>
  );
};
