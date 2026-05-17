"use client";

import Image from "next/image";
import { useState } from "react";

import { useFlexWrapOverflow } from "@/hooks/useFlexWrapOverflow";

import arrow from "../assets/brown-arrow.svg";
import checked from "../assets/checked.svg";
import icon from "../assets/icon.svg";
import plus from "../assets/plus.svg";

const labelClasses = {
  unchecked: "border-line-gray bg-white",
  checked: "border-accent-orange bg-accent-orange-back text-brown-middle",
};

export const TagFilter = () => {
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const tags = [
    { id: 1, name: "タグ1", count: 10 },
    { id: 2, name: "タグ2", count: 15 },
    { id: 3, name: "タグ3", count: 7 },
  ];

  // 折り返し展開フラグ
  const [isOpen, setIsOpen] = useState(false);

  // 要素の数に応じて折り返しの有無を判定するカスタムフック
  const { ref, hasOverflow, isLoading, closedHeight, fullHeight } = useFlexWrapOverflow(
    tags.length,
  );

  const handleChange = (tagId: number, checked: boolean) => {
    const next = checked ? [...selectedTagIds, tagId] : selectedTagIds.filter((id) => id !== tagId);
    setSelectedTagIds(next);
  };
  return (
    <div className="bg-brown-back w-full rounded-lg px-7 pt-6 pb-8 max-md:p-3 max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="max-md:text-[13px]">タグ</p>
      </div>
      <div
        ref={ref}
        className={`mt-3 flex flex-wrap gap-2 overflow-hidden transition-all`}
        style={{ maxHeight: isOpen ? fullHeight : closedHeight }}
      >
        {tags.map((tag) => (
          <label
            key={tag.id}
            htmlFor={tag.id.toString()}
            className={`flex cursor-pointer items-center gap-2 rounded-4xl border py-1.5 pr-5 pl-3 transition-all max-md:py-1 max-md:pr-3 ${selectedTagIds.includes(tag.id) ? labelClasses.checked : labelClasses.unchecked}`}
          >
            <input
              type="checkbox"
              id={tag.id.toString()}
              checked={selectedTagIds.includes(tag.id)}
              onChange={(e) => handleChange(tag.id, e.target.checked)}
              className="hidden"
            />
            <Image
              src={selectedTagIds.includes(tag.id) ? checked : plus}
              alt=""
              width={14}
              height={14}
            />
            <p className="text-sm max-md:text-xs">{tag.name}</p>
            <p
              className={`rounded-4xl px-1 py-px text-[10px] tracking-tighter text-white ${selectedTagIds.includes(tag.id) ? "bg-accent-orange" : "bg-disabled-text"}`}
            >
              {tag.count}
            </p>
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
