"use client";

import Image from "next/image";
import { useId, useState } from "react";

import { useFlexWrapOverflow } from "@/hooks/useFlexWrapOverflow";
import { SharingGroupResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import arrow from "../assets/brown-arrow.svg";
import icon from "../assets/icon.svg";

type Props = {
  sharingGroups: SharingGroupResponseDto[];
  updateFilter: ({ key, value }: { key: string; value: string }) => void;
  currentValue?: string;
};

export const SharingGroupFilter = ({ sharingGroups, updateFilter, currentValue = "" }: Props) => {
  const uid = useId();

  // 折り返し展開フラグ
  const [isOpen, setIsOpen] = useState<boolean>(false);

  // 要素の数に応じて折り返しの有無を判定するカスタムフック
  const { ref, hasOverflow, isLoading, closedHeight, fullHeight } = useFlexWrapOverflow(
    sharingGroups.length,
  );

  // 共有グループ選択時の処理
  return (
    <div className="bg-background-accent w-full rounded-lg px-7 pt-6 pb-8 @max-md:p-3 @max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="@max-md:text-[13px]">共有範囲</p>
      </div>
      <div
        ref={ref}
        className="mt-3 flex flex-wrap gap-2 overflow-hidden transition-all"
        style={{ maxHeight: isOpen ? fullHeight : closedHeight }}
      >
        <label
          htmlFor={`allSharingGroup-${uid}`}
          className="has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle border-line-gray bg-light-dark flex cursor-pointer items-center gap-2 rounded-lg border py-1.5 pr-5 pl-3 transition-all @max-md:py-1"
        >
          <input
            type="radio"
            id={`allSharingGroup-${uid}`}
            name={`sharing-group-${uid}`}
            checked={currentValue === ""}
            className="accent-accent-orange-radio"
            onChange={() => {
              updateFilter({ key: "sharingGroupId", value: "" });
            }}
          />
          <p className="text-sm @max-md:text-xs">すべて</p>
        </label>
        <label
          htmlFor={`allSharingGroupAll-${uid}`}
          className="has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle border-line-gray bg-light-dark flex cursor-pointer items-center gap-2 rounded-lg border py-1.5 pr-5 pl-3 transition-all @max-md:py-1"
        >
          <input
            type="radio"
            id={`allSharingGroupAll-${uid}`}
            name={`sharing-group-${uid}`}
            checked={currentValue === "0"}
            className="accent-accent-orange-radio"
            onChange={() => {
              // 0を指定するでバックエンドで指定なしを取得する
              updateFilter({ key: "sharingGroupId", value: "0" });
            }}
          />
          <p className="text-sm @max-md:text-xs">全員に公開</p>
        </label>
        {sharingGroups.map((group) => (
          <label
            key={group.id}
            htmlFor={`sharing-group-${group.id}-${uid}`}
            className="has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle border-line-gray bg-light-dark flex cursor-pointer items-center gap-2 rounded-lg border py-1.5 pr-5 pl-3 transition-all @max-md:py-1"
          >
            <input
              type="radio"
              id={`sharing-group-${group.id}-${uid}`}
              name={`sharing-group-${uid}`}
              checked={currentValue === group.id.toString()}
              className="accent-accent-orange-radio"
              onChange={() => {
                updateFilter({ key: "sharingGroupId", value: group.id.toString() });
              }}
            />
            <p className="text-sm @max-md:text-xs">{group.name}</p>
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
