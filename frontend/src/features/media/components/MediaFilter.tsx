"use client";
import Image from "next/image";
import { useState } from "react";

import { SharingGroupFilter } from "@/features/sharing";
import { TagFilter } from "@/features/tag";

import heart from "../assets/heart.png";
import illust from "../assets/illust.png";

import { DateRangeFilter } from "./DateRangeFilter";
import { MediaKindFilter } from "./MediaKindFilter";

export const MediaFilter = () => {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <div className="relative z-10">
      <div
        className={`bg-white-back border-brown-dark relative mt-10 rounded-lg border p-7 ${isOpen ? "max-md:px-4 max-md:pt-6 max-md:pb-0" : "max-md:p-0"}`}
      >
        <Image
          src={illust}
          alt=""
          width={120}
          height={90}
          className="absolute -top-18 right-5 max-md:-top-14 max-md:right-3 max-md:h-16.75 max-md:w-22.5"
        />
        <Image
          src={heart}
          alt=""
          width={92}
          height={76}
          className="absolute -top-5 -right-24 -z-1 max-xl:top-5 max-xl:-right-15 max-md:top-2 max-md:-right-13 max-md:h-16.5 max-md:w-20"
        />
        <div
          className={`grid transition-all duration-400 ${isOpen ? "max-md:grid-rows-[1fr]" : "max-md:grid-rows-[0fr]"}`}
        >
          <div className="overflow-hidden">
            {/* 上段 */}

            <div className="flex grid-rows-[0fr] gap-5 max-md:flex-col max-md:gap-4">
              {/* 写真/動画 */}
              <MediaKindFilter />

              {/* 共有範囲 */}
              <SharingGroupFilter />
            </div>

            {/* 下段 */}
            <div className="mt-5 flex gap-5 max-md:flex-col max-md:gap-4">
              {/* タグ */}
              <TagFilter />

              {/* 期間 */}
              <DateRangeFilter />
            </div>
          </div>
        </div>
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="text-brown-dark flex w-full items-center justify-center gap-2 py-5 text-[13px] outline-0 md:hidden"
        >
          <p>{isOpen ? "閉じる" : "絞込検索"}</p>

          <svg
            width="12"
            height="12"
            viewBox="0 0 12 12"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <rect width="12" height="12" rx="6" fill="#6F4000" />
            <path
              d="M3.19995 5.99992H8.79995"
              stroke="white"
              strokeMiterlimit="10"
              strokeLinecap="round"
            />
            {!isOpen && (
              <path
                d="M6 3.20004V8.80004"
                stroke="white"
                strokeMiterlimit="10"
                strokeLinecap="round"
              />
            )}
          </svg>
        </button>
      </div>
    </div>
  );
};
