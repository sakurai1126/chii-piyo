"use client";

import Image from "next/image";

import healthIcon from "../../assets/health.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const HealthAction = () => {
  const { isOpen, setIsOpen, date, setDate, time, setTime, openModal } = useCareRecord();

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
        saveAction={() => {}}
      >
        <div className="my-4 flex items-end gap-2 max-md:justify-center">
          <span className="">体温</span>
          <input
            type="number"
            min={34}
            max={42}
            step={0.1}
            className="border-line-gray h-10 rounded-sm border bg-white pl-2 text-2xl font-medium max-md:text-xl"
          />
          <span className="">℃</span>
        </div>
      </CareActionModal>
      <button
        className="border-health-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
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
