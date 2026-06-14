"use client";

import { Dispatch, SetStateAction } from "react";

import { useIntersectionObserver } from "@/hooks/useIntersectionObserver";
import { MediaResponseDto, UserResponseDto } from "@/lib/api-client/gen";
import { MediaListResponseDto } from "@/lib/api-client/gen/models/MediaListResponseDto";

import { useInfiniteMediaList, UseInfiniteMediaListParams } from "../../hooks/useInfiniteMediaList";

import { MediaListItem } from "./MediaListItem";

type MediaListProps = {
  initialData: MediaListResponseDto;
  isSelectionMode?: boolean;
  params?: UseInfiniteMediaListParams;
  users: UserResponseDto[];
  setSelectedMedia: Dispatch<SetStateAction<number[]>>;
};

export const MediaList = ({
  initialData,
  isSelectionMode,
  params,
  users,
  setSelectedMedia,
}: MediaListProps) => {
  const hasActiveFilters =
    params !== undefined &&
    Object.entries(params).some(
      ([, v]) => v !== undefined && !(Array.isArray(v) && v.length === 0),
    );

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isError, error } =
    useInfiniteMediaList({
      params,
      initialData: hasActiveFilters ? undefined : initialData,
    });

  // スクロール時追加読み込みカスタムフック、発火位置に指定するrefを受け取る
  const loadMoreRef = useIntersectionObserver({
    // コールバックで読み込む次ページの読み込み関数
    callback: fetchNextPage,
    // 次のページが存在、かつ次のページを読み込んでいないときのみ有効化
    enabled: !!hasNextPage && !isFetchingNextPage,
  });

  // フェッチされた全ページのデータからitemsのみを抜き取って1つの配列にまとめる
  const flatItems = data?.pages.flatMap((page) => page.items) ?? [];

  // 年月ごとにグルーピング
  const grouped = groupByYearMonth(flatItems);

  return (
    <>
      {grouped.map(({ label, items }) => (
        <div key={label}>
          {/* 年月表示 */}
          <div className="mt-10 flex items-center gap-10">
            <p className="text-note-gray shrink-0 text-2xl font-light max-md:text-sm">{label}</p>
            <div className="bg-line-gray h-px w-full max-md:hidden"></div>
          </div>
          {/* メディアリスト */}
          <div className="mt-4 ml-7 grid grid-cols-4 gap-2 max-md:mt-2 max-md:ml-0 max-md:grid-cols-3 max-md:gap-0.5">
            {items.map((item) => (
              <MediaListItem
                key={item.id}
                data={item}
                isSelectionMode={isSelectionMode}
                users={users}
                setSelectedMedia={setSelectedMedia}
              />
            ))}
          </div>
        </div>
      ))}

      {/* 追加読み込み発火位置 */}
      <div ref={loadMoreRef} className="h-1" />
      {isFetchingNextPage && (
        <p className="text-note-gray py-4 text-center text-sm">読み込み中...</p>
      )}

      {/* エラーメッセージ */}
      {isError && (
        <p className="text-warning py-4 text-center text-sm">
          {error?.message ?? "読み込みに失敗しました"}
        </p>
      )}
    </>
  );
};

// 年月ごとにグルーピング
const groupByYearMonth = (items: MediaResponseDto[]) => {
  const map = new Map<string, MediaResponseDto[]>();

  for (const item of items) {
    const date = new Date(item.createdAt);
    const label = `${date.getFullYear()}年${date.getMonth() + 1}月`;
    const list = map.get(label) ?? [];
    list.push(item);
    map.set(label, list);
  }

  return Array.from(map, ([label, items]) => ({ label, items }));
};
