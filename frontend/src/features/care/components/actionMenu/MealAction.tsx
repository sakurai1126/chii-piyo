"use client";

import Image from "next/image";

import mealIcon from "../../assets/meal.svg";
import { useCareRecord } from "../../hooks/useCareRecord";
import { CareActionModal } from "../ui/CareActionModal";

export const MealAction = () => {
  const { isOpen, setIsOpen, date, setDate, time, setTime, openModal } = useCareRecord();

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
        saveAction={() => {}}
      />

      <button
        className="border-meal-border group w-full cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:rounded-4xl max-md:p-4"
        onClick={openModal}
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
