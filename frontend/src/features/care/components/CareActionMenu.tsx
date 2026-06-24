import Image from "next/image";

import diaperIcon from "../assets/diaper.svg";
import growthIcon from "../assets/growth.svg";
import healthIcon from "../assets/health.svg";
import mealIcon from "../assets/meal.svg";
import milkIcon from "../assets/milk.svg";

export const CareActionMenu = () => {
  return (
    <div className="mt-15 grid grid-cols-5 gap-3 max-md:mt-7 max-md:flex max-md:flex-wrap max-md:gap-4">
      <button className="border-meal-border group cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:w-[calc(50%-8px)] max-md:rounded-4xl max-md:p-4">
        <Image
          src={mealIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-meal-text mt-3 text-lg font-medium max-md:text-[16px]">食事</p>
      </button>
      <button className="border-milk-border group cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:w-[calc(50%-8px)] max-md:rounded-4xl max-md:p-4">
        <Image
          src={milkIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-milk-text mt-3 text-lg font-medium max-md:text-[16px]">ミルク</p>
      </button>
      <button className="border-diaper-border group cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:w-[calc(50%-8px)] max-md:rounded-4xl max-md:p-4">
        <Image
          src={diaperIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-diaper-text mt-3 text-lg font-medium max-md:text-[16px]">排泄</p>
      </button>
      <button className="border-health-border group cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:w-[calc(50%-8px)] max-md:rounded-4xl max-md:p-4">
        <Image
          src={healthIcon}
          alt=""
          className="mx-auto transition-all group-hover:scale-110 max-md:h-20 max-md:w-20"
          width={100}
          height={100}
        />
        <p className="text-health-text mt-3 text-lg font-medium max-md:text-[16px]">体調</p>
      </button>
      <button className="border-growth-border group cursor-pointer rounded-lg border bg-white/50 p-5 backdrop-blur-[7.5px] transition-all max-md:flex max-md:w-full max-md:items-center max-md:justify-center max-md:gap-3 max-md:rounded-4xl max-md:p-4">
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
