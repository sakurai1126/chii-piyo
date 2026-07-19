"use client";

import Image from "next/image";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { useState } from "react";

import { Button } from "@/components/ui/Button";
import { SharingGroupFilter } from "@/features/sharing";
import { TagFilter } from "@/features/tag";
import { SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import heart from "../../assets/heart.png";
import illust from "../../assets/illust.png";

import { DateRangeFilter } from "./DateRangeFilter";
import { MediaKindFilter } from "./MediaKindFilter";

type Props = {
  isEasy: boolean;
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  showMediaCount?: boolean;
};

export const MediaFilter = ({ isEasy, tags, sharingGroups, showMediaCount = false }: Props) => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const pathname = usePathname();

  const updateFilter = ({ key, value }: { key: string; value: string }) => {
    const params = new URLSearchParams(searchParams.toString());

    if (key === "tagId") {
      const current = params.getAll("tagId");
      if (current.includes(value)) {
        // すでに選択されているタグが再度選択された場合は、そのタグを削除する
        // 一旦すべて削除してから、選択されたタグ以外を再度追加する
        params.delete("tagId");
        current.filter((v) => v !== value).forEach((v) => params.append("tagId", v));
      } else {
        params.append("tagId", value);
      }
    } else if (value) {
      params.set(key, value);
    } else {
      params.delete(key);
    }

    router.push(`?${params.toString()}`, { scroll: false });
  };

  const paramsReset = () => {
    router.push(pathname, { scroll: false });
  };

  const [isOpen, setIsOpen] = useState(false);
  return (
    <div className="relative z-10">
      <div
        className={cn(
          "bg-background-normal border-brown-dark relative mt-10 rounded-lg border p-7 @max-md:p-0",
          isOpen && "@max-md:px-4 @max-md:pt-6 @max-md:pb-0",
          isEasy && "border-none",
        )}
      >
        <Image
          src={illust}
          alt=""
          width={120}
          height={90}
          className={cn(
            "pointer-events-none absolute -top-18 @max-md:-top-14 @max-md:h-16.75 @max-md:w-22.5",
            isEasy ? "right-auto left-0 @max-md:top-10" : "right-5 @max-md:right-3",
          )}
        />
        <Image
          src={heart}
          alt=""
          width={92}
          height={76}
          className={cn(
            "pointer-events-none absolute -top-5 -right-24 -z-1 @max-xl:top-5 @max-xl:-right-15 @max-md:top-2 @max-md:-right-13 @max-md:h-16.5 @max-md:w-20",
            isEasy && "@max-md:top-12 @max-md:right-0 @max-md:h-12.5 @max-md:w-15",
          )}
        />
        {!isEasy && (
          <>
            <div
              className={cn(
                "grid transition-all duration-400",
                isOpen ? "@max-md:grid-rows-[1fr]" : "@max-md:grid-rows-[0fr]",
              )}
            >
              <div className="overflow-hidden">
                {/* 上段 */}

                <div className="flex grid-rows-[0fr] gap-5 @max-md:flex-col @max-md:gap-4">
                  {/* 写真/動画 */}
                  <MediaKindFilter
                    updateFilter={updateFilter}
                    currentValue={searchParams.get("mediaKind") ?? ""}
                  />

                  {/* 共有範囲 */}
                  <SharingGroupFilter
                    sharingGroups={sharingGroups}
                    updateFilter={updateFilter}
                    currentValue={searchParams.get("sharingGroupId") ?? ""}
                  />
                </div>

                {/* 下段 */}
                <div className="mt-5 flex gap-5 @max-md:flex-col @max-md:gap-4">
                  {/* タグ */}
                  <TagFilter
                    tags={tags}
                    updateFilter={updateFilter}
                    currentValue={searchParams.getAll("tagId") ?? ""}
                    showMediaCount={showMediaCount}
                  />

                  {/* 期間 */}
                  <DateRangeFilter
                    updateFilter={updateFilter}
                    currentStartDate={searchParams.get("startDate") ?? ""}
                    currentEndDate={searchParams.get("endDate") ?? ""}
                  />
                </div>
              </div>
            </div>

            <button
              type="button"
              onClick={() => setIsOpen(!isOpen)}
              className="text-brown-dark dark:text-brown-light flex w-full items-center justify-center gap-2 py-5 text-[13px] outline-0 @md:hidden"
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
            {searchParams.size > 0 && (
              <Button
                variant="cancel"
                className="text-note-gray dark:text-line-gray mt-3 ml-auto block w-60"
                onClick={paramsReset}
              >
                検索条件をリセット
              </Button>
            )}
          </>
        )}
      </div>
    </div>
  );
};
