"use client";
import Image from "next/image";
import { useId, useState } from "react";

import { useFlexWrapOverflow } from "@/hooks/useFlexWrapOverflow";

import arrow from "../assets/brown-arrow.svg";
import icon from "../assets/icon.svg";

const labelClasses = {
  unchecked: "border-line-gray bg-white",
  checked: "border-accent-orange bg-accent-orange-back text-brown-middle",
};

export const SharingGroupFilter = () => {
  const uid = useId();
  const [checkedValue, setCheckedValue] = useState<number | null>(null);

  const sharingGroups = [
    { id: 1, name: "家族全員" },
    { id: 2, name: "夫婦" },
    { id: 3, name: "自分のみ" },
  ];

  // 折り返し展開フラグ
  const [isOpen, setIsOpen] = useState(false);

  // 要素の数に応じて折り返しの有無を判定するカスタムフック
  const { ref, hasOverflow, isLoading, closedHeight, fullHeight } = useFlexWrapOverflow(
    sharingGroups.length,
  );

  // 共有グループ選択時の処理
  const handleChange = (groupId: number) => setCheckedValue(groupId);

  return (
    <div className="bg-brown-back w-full rounded-lg px-7 pt-6 pb-8 max-md:p-3 max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="max-md:text-[13px]">共有範囲</p>
      </div>
      <div
        ref={ref}
        className={`mt-3 flex flex-wrap gap-2 overflow-hidden transition-all`}
        style={{ maxHeight: isOpen ? fullHeight : closedHeight }}
      >
        {sharingGroups.map((group) => (
          <label
            key={group.id}
            htmlFor={`sharing-group-${group.id}`}
            className={`flex cursor-pointer items-center gap-2 rounded-lg border py-1.5 pr-5 pl-3 transition-all max-md:py-1 ${
              checkedValue === group.id ? labelClasses.checked : labelClasses.unchecked
            }`}
          >
            <input
              type="radio"
              id={`sharing-group-${group.id}`}
              name={`sharing-group-${uid}`}
              className="accent-accent-orange-radio"
              checked={checkedValue === group.id}
              onChange={() => handleChange(group.id)}
            />
            <p className="text-sm max-md:text-xs">{group.name}</p>
          </label>
        ))}
      </div>
      {!isLoading && hasOverflow && (
        <button
          type="button"
          onClick={() => setIsOpen((prev) => !prev)}
          className="text-brown-middle mt-2 -mb-5 flex h-5 w-full justify-center text-sm underline max-md:-mb-2"
        >
          <Image
            src={arrow}
            alt=""
            width={13}
            height={7}
            className={`${isOpen ? "rotate-180" : ""} transition-all`}
          />
        </button>
      )}
    </div>
  );
};
