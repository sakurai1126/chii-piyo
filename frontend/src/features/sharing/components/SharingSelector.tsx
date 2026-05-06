import { useId } from "react";

export const SharingSelector = () => {
  const uid = useId();
  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">共有範囲を編集</p>
      <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2">
        <label htmlFor={`${uid}-1`} className="flex items-center gap-2">
          <input
            type="radio"
            id={`${uid}-1`}
            name={`${uid}-sharing`}
            className="accent-accent-pink h-4 w-4"
            defaultChecked
          />
          <p className="max-md:text-[13px]">家族全員</p>
        </label>
        <label htmlFor={`${uid}-2`} className="flex items-center gap-2">
          <input
            type="radio"
            id={`${uid}-2`}
            name={`${uid}-sharing`}
            className="accent-accent-pink h-4 w-4"
          />
          <p className="max-md:text-[13px]">夫婦</p>
        </label>
        <label htmlFor={`${uid}-3`} className="flex items-center gap-2">
          <input
            type="radio"
            id={`${uid}-3`}
            name={`${uid}-sharing`}
            className="accent-accent-pink h-4 w-4"
          />
          <p className="max-md:text-[13px]">自分のみ</p>
        </label>
      </div>
    </>
  );
};
