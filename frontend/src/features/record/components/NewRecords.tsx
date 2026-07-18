"use client";

import Image from "next/image";
import { useState } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import arrow from "../assets/arrow.svg";
import plusIcon from "../assets/plus.svg";

import { RecordEditMenu } from "./RecordEditMenu";

type Props = {
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  variant: "first" | "word";
};

export const NewRecords = ({ tags, sharingGroups, variant }: Props) => {
  // メニュー開閉フラグ
  const [isMenuOpen, setIsMenuOpen] = useState<boolean>(false);

  return (
    <div
      className={cn(
        "border-brown-dark bg-background-normal w-full rounded-lg border border-dashed",
        variant === "first" && "@max-md:mt-6",
      )}
    >
      {/* 開くボタン */}
      <AccordionContent isOpen={!isMenuOpen}>
        <button
          type="button"
          className={cn(
            "hover:bg-background-normal bg-green-back dark:bg-background-accent flex h-20 w-full cursor-pointer items-center justify-center gap-3 rounded-lg transition-all",
            variant === "word" && "h-30 @max-md:h-20",
          )}
          onClick={() => setIsMenuOpen(true)}
        >
          <p className="text-brown-light font-medium @max-md:text-[13px]">
            {variant === "first" && <>新しいはじめてを記録する</>}
            {variant === "word" && <>新しいことばを記録する</>}
          </p>
          <Image src={plusIcon} alt="" width={14} height={14} />
        </button>
      </AccordionContent>

      {/* 新規追加UI */}
      <AccordionContent isOpen={isMenuOpen}>
        <div className="px-7 pt-7 @max-md:px-4 @max-md:pt-5">
          <RecordEditMenu
            tags={tags}
            sharingGroups={sharingGroups}
            setIsMenuOpen={setIsMenuOpen}
            variant={variant === "first" ? "newFirstRecord" : "newWordRecord"}
          />
        </div>
        {/* 閉じるボタン */}
        <button
          type="button"
          className="mt-3 grid w-full cursor-pointer place-content-center p-3 pb-5"
          onClick={() => setIsMenuOpen(false)}
        >
          <Image src={arrow} alt="" className="" width={13} height={7} />
        </button>
      </AccordionContent>
    </div>
  );
};
