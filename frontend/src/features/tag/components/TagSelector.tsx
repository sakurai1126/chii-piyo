import { useId } from "react";

export const TagSelector = () => {
  const uid = useId();
  return (
    <>
      <p className="mt-8 max-md:mt-4 max-md:text-[13px]">タグを編集</p>
      <div className="mt-3 flex flex-wrap gap-x-4 gap-y-2">
        <label htmlFor={`${uid}-1`} className="flex items-center gap-2">
          <input
            type="checkbox"
            id={`${uid}-1`}
            name={`${uid}-tag`}
            className="accent-accent-pink h-4 w-4"
          />
          <p className="max-md:text-[13px]">タグ1</p>
        </label>
        <label htmlFor={`${uid}-2`} className="flex items-center gap-2">
          <input
            type="checkbox"
            id={`${uid}-2`}
            name={`${uid}-tag`}
            className="accent-accent-pink h-4 w-4"
          />
          <p className="max-md:text-[13px]">タグ2</p>
        </label>
        <label htmlFor={`${uid}-3`} className="flex items-center gap-2">
          <input
            type="checkbox"
            id={`${uid}-3`}
            name={`${uid}-tag`}
            className="accent-accent-pink h-4 w-4"
          />
          <p className="max-md:text-[13px]">タグ3</p>
        </label>
      </div>
    </>
  );
};
