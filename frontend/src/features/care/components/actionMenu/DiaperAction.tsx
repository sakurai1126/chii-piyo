"use client";

import Image from "next/image";

import diaperIcon from "../../assets/diaper.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const DiaperAction = () => {
  const { isOpen, setIsOpen, date, setDate, time, setTime, openModal } = useCareRecord();

  return (
    <div className="max-md:w-[calc(50%-8px)]">
      <CareActionModal
        title="排泄"
        isOpen={isOpen}
        icon={diaperIcon}
        date={date}
        setDate={setDate}
        time={time}
        setTime={setTime}
        onCancel={() => setIsOpen(false)}
        saveAction={() => {}}
      >
        <div className="my-4 flex gap-4">
          <button className="bg-green-back border-brown-dark/70 text-brown-dark/70 h-10 w-40 rounded-4xl border font-medium">
            おしっこ
          </button>
          <button className="bg-accent-orange-back border-brown-dark text-brown-dark h-10 w-40 rounded-4xl border font-medium">
            うんち
          </button>
        </div>
      </CareActionModal>

      <button
        className="border-diaper-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
      >
        <Image
          src={diaperIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-diaper-text mt-3 text-lg font-medium max-md:text-[16px]">排泄</p>
      </button>
    </div>
  );
};
