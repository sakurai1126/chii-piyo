import { DiaperAction } from "./actionMenu/DiaperAction";
import { GrowthAction } from "./actionMenu/GrowthAction";
import { HealthAction } from "./actionMenu/HealthAction";
import { MealAction } from "./actionMenu/MealAction";
import { MilkAction } from "./actionMenu/MilkAction";

export const CareActionMenu = () => {
  return (
    <div className="mt-15 grid grid-cols-5 gap-3 max-md:mt-7 max-md:flex max-md:flex-wrap max-md:gap-4">
      <MealAction />
      <MilkAction />
      <DiaperAction />
      <HealthAction />
      <GrowthAction />
    </div>
  );
};
