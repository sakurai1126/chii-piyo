"use client";

import Image from "next/image";

import minus from "../../assets/milk-minus.svg";
import plus from "../../assets/milk-plus.svg";
import milkIcon from "../../assets/milk.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const MilkAction = () => {
  const { isOpen, setIsOpen, date, setDate, time, setTime, openModal } = useCareRecord();
  const milkAmounts = [100, 140, 160, 200];

  return (
    <div className="max-md:w-[calc(50%-8px)]">
      <CareActionModal
        title="ミルク"
        isOpen={isOpen}
        icon={milkIcon}
        date={date}
        setDate={setDate}
        time={time}
        setTime={setTime}
        onCancel={() => setIsOpen(false)}
        saveAction={() => {}}
      >
        <div className="mt-4 flex items-center gap-4 max-md:justify-center">
          <button className="cursor-pointer transition-all hover:opacity-70">
            <Image src={minus} alt="" width={26} height={26} />
          </button>
          <div>
            <span className="text-4xl font-medium">200</span>
            <span className="ml-1 text-lg">ml</span>
          </div>
          <button className="cursor-pointer transition-all hover:opacity-70">
            <Image src={plus} alt="" width={26} height={26} />
          </button>
        </div>
        <div className="my-5 flex gap-3 max-md:gap-2">
          {milkAmounts.map((value, index) => (
            <button
              className="bg-accent-orange/10 border-brown-dark text-brown-dark h-8 w-20 cursor-pointer rounded-sm border font-medium transition-all hover:opacity-70 max-md:text-sm"
              key={index}
            >
              {value}
            </button>
          ))}
        </div>
      </CareActionModal>

      <button
        className="border-milk-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
      >
        <Image
          src={milkIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-milk-text mt-3 text-lg font-medium max-md:text-[16px]">ミルク</p>
      </button>
    </div>
  );
};
