import Link from "next/link";

import { AlbumsGrid } from "@/features/album";
import { CareActionMenu } from "@/features/care";
import { GraphSummary } from "@/features/graph";
import {
  AlbumResponseDto,
  CareRecordListResponseDto,
  GrowthRecordResponseDto,
  WordRecordResponseDto,
} from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

type Props = {
  isAdmin: boolean;
  isEasy: boolean;
  careRecords: CareRecordListResponseDto;
  growthRecords: GrowthRecordResponseDto[];
  wordRecords: WordRecordResponseDto[];
  albums: AlbumResponseDto[];
};

export const TopContents = ({
  isAdmin,
  isEasy,
  careRecords,
  growthRecords,
  wordRecords,
  albums,
}: Props) => {
  const linkButtonBaseStyle = `flex h-12 cursor-pointer items-center justify-center rounded-lg border font-medium transition-all duration-300 ${isEasy ? "border-brown-dark bg-brown-light text-[16px] text-white" : "border-brown-middle text-brown-middle bg-brown-back hover:bg-brown-light text-sm hover:text-white @max-md:h-10"}`;

  const titleLineBaseStyle =
    "mx-auto mt-5 h-0.5 w-30 rounded-xs bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] @max-md:mt-4 @max-md:h-px @max-md:w-20";

  return (
    <>
      {/* 記録メニュー */}
      {isAdmin && !isEasy && (
        <div className="mt-20 @max-md:mt-15">
          <h2 className="font-title text-center text-4xl @max-md:text-[20px]">お世話を記録する</h2>
          <div className={cn(titleLineBaseStyle, "-mb-5 @max-md:mb-1")}></div>
          <CareActionMenu />
          <Link
            href="/care"
            className={cn(
              linkButtonBaseStyle,
              "mx-auto mt-10 w-50 @max-md:mt-6 @max-md:gap-2 @max-md:text-sm",
            )}
          >
            記録一覧
          </Link>
        </div>
      )}
      {/* アルバム */}
      <div className="mt-20 @max-md:mt-15">
        <h2
          className={cn(
            "font-title text-center text-4xl",
            isEasy ? "text-[26px]" : "@max-md:text-[20px]",
          )}
        >
          アルバム
        </h2>
        <div className={cn(titleLineBaseStyle, "-mb-5 @max-md:-mb-9")}></div>
        {albums.length ? (
          <AlbumsGrid isEasy={isEasy} albums={albums} variant="top" />
        ) : (
          <p className="mt-20 mb-15 text-center @max-md:text-sm">アルバムは作成されていません</p>
        )}

        <Link
          href="/albums"
          className={cn(
            linkButtonBaseStyle,
            "mx-auto mt-10 w-50 @max-md:mt-6 @max-md:gap-2 @max-md:text-sm",
          )}
        >
          アルバム一覧
        </Link>
      </div>

      {/* サマリー表示 */}
      <div className={cn("mt-20 @max-md:mt-15", isEasy && "px-5")}>
        <h2
          className={cn(
            "font-title text-center text-4xl",
            isEasy ? "text-[26px]" : "@max-md:text-[20px]",
          )}
        >
          最近の記録
        </h2>
        <div className={cn(titleLineBaseStyle)}></div>
        <GraphSummary
          isAdmin={isAdmin}
          isEasy={isEasy}
          growthRecords={growthRecords}
          careRecords={careRecords}
          wordRecords={wordRecords}
        />
        <div
          className={cn(
            "mt-10 flex justify-center gap-5 @max-md:mt-6",
            isEasy ? "flex-col items-center gap-3" : "@max-md:gap-2",
          )}
        >
          <Link
            href="/analysis"
            className={cn(linkButtonBaseStyle, "w-40", !isEasy && "@max-md:text-xs")}
          >
            グラフ
          </Link>
          <Link
            href="/first-records"
            className={cn(linkButtonBaseStyle, "w-40", !isEasy && "@max-md:text-xs")}
          >
            はじめて
          </Link>
          <Link
            href="/word-records"
            className={cn(linkButtonBaseStyle, "w-40", !isEasy && "@max-md:text-xs")}
          >
            ことば
          </Link>
        </div>
      </div>
    </>
  );
};
