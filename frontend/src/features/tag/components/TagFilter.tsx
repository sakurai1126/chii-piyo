"use client";

import Image from "next/image";
import { useId, useState } from "react";

import { useFlexWrapOverflow } from "@/hooks/useFlexWrapOverflow";
import { TagResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import arrow from "../assets/brown-arrow.svg";
import checked from "../assets/checked.svg";
import icon from "../assets/icon.svg";
import plus from "../assets/plus.svg";

type Props = {
  tags: TagResponseDto[];
  updateFilter: ({ key, value }: { key: string; value: string }) => void;
  currentValue?: string | string[];
};

export const TagFilter = ({ tags, updateFilter, currentValue = [] }: Props) => {
  const uid = useId();
  // 折り返し展開フラグ
  const [isOpen, setIsOpen] = useState(false);

  // 要素の数に応じて折り返しの有無を判定するカスタムフック
  const { ref, hasOverflow, isLoading, closedHeight, fullHeight } = useFlexWrapOverflow(
    tags.length,
  );

  const handleChange = (tagId: number) => {
    updateFilter({ key: "tagId", value: tagId.toString() });
  };
  return (
    <div className="bg-background-accent w-full rounded-lg px-7 pt-6 pb-8 @max-md:p-3 @max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="@max-md:text-[13px]">タグ</p>
      </div>
      <div
        ref={ref}
        className="mt-3 flex flex-wrap gap-2 overflow-hidden transition-all"
        style={{ maxHeight: isOpen ? fullHeight : closedHeight }}
      >
        {tags.map((tag) => (
          <label
            key={tag.id}
            htmlFor={`${tag.id.toString()}-${uid}`}
            className="border-line-gray has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle bg-light-dark flex cursor-pointer items-center gap-2 rounded-4xl border py-1.5 pr-5 pl-3 transition-all @max-md:py-1 @max-md:pr-3"
          >
            <input
              type="checkbox"
              id={`${tag.id.toString()}-${uid}`}
              checked={currentValue.includes(tag.id.toString())}
              onChange={() => handleChange(tag.id)}
              className="hidden"
            />
            <Image
              src={currentValue.includes(tag.id.toString()) ? checked : plus}
              alt=""
              width={14}
              height={14}
            />
            <p className="text-sm @max-md:text-xs">{tag.name}</p>
            <p
              className={cn(
                "rounded-4xl px-1 py-px text-[10px] tracking-tighter text-white",
                currentValue.includes(tag.id.toString()) ? "bg-accent-orange" : "bg-note-gray",
              )}
            >
              {tag.mediaCount}
            </p>
          </label>
        ))}
      </div>
      {!isLoading && hasOverflow && (
        <button
          type="button"
          onClick={() => setIsOpen((prev) => !prev)}
          className="text-brown-middle mt-2 -mb-5 flex h-5 w-full justify-center text-sm underline @max-md:-mb-2"
        >
          <Image
            src={arrow}
            alt=""
            width={13}
            height={7}
            className={cn("transition-all", isOpen && "rotate-180")}
          />
        </button>
      )}
    </div>
  );
};
