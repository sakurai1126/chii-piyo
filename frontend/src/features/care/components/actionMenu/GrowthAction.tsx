"use client";

import Image from "next/image";

import growthIcon from "../../assets/growth.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const GrowthAction = () => {
  const { isOpen, setIsOpen, date, setDate, time, setTime, openModal } = useCareRecord();

  return (
    <div className="max-md:w-full">
      <CareActionModal
        title="身長/体重"
        isOpen={isOpen}
        icon={growthIcon}
        date={date}
        setDate={setDate}
        time={time}
        setTime={setTime}
        onCancel={() => setIsOpen(false)}
        saveAction={() => {}}
      >
        <div className="my-4 flex gap-6 max-md:justify-center max-md:gap-3">
          <div className="flex items-end gap-2">
            <span className="max-md:text-sm">身長</span>
            <input
              type="number"
              min={34}
              max={42}
              step={0.1}
              className="border-line-gray h-10 rounded-sm border bg-white pl-2 text-2xl font-medium max-md:pr-1 max-md:text-xl"
            />
            <span className="max-md:text-sm">cm</span>
          </div>
          <div className="flex items-end gap-2">
            <span className="max-md:text-sm">体重</span>
            <input
              type="number"
              min={34}
              max={42}
              step={0.1}
              className="border-line-gray h-10 rounded-sm border bg-white pl-2 text-2xl font-medium max-md:pr-1 max-md:text-xl"
            />
            <span className="max-md:text-sm">kg</span>
          </div>
        </div>
      </CareActionModal>

      <button
        className="border-growth-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:flex max-md:items-center max-md:justify-center max-md:gap-3 max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
      >
        <Image
          src={growthIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:m-0 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-growth-text mt-3 text-lg font-medium max-md:text-[16px]">身長/体重</p>
      </button>
    </div>
  );
};
