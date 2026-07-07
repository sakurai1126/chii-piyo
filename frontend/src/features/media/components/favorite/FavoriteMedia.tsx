"use client";

import { useIntersectionObserver } from "@/hooks/useIntersectionObserver";
import { MediaListResponseDto, UserResponseDto } from "@/lib/api-client/gen";

import { useInfiniteMediaList } from "../../hooks/useInfiniteMediaList";
import { MediaListItem } from "../list/MediaListItem";

type Props = {
  initialData: MediaListResponseDto;
  users: UserResponseDto[];
};

export const FavoriteMedia = ({ initialData, users }: Props) => {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isError, error } =
    useInfiniteMediaList({
      params: { isFavorite: true },
      initialData,
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

  return (
    <>
      {/* メディアリスト */}
      <div className="mt-15 grid grid-cols-4 gap-2 max-md:mt-2 max-md:ml-0 max-md:grid-cols-3 max-md:gap-0.5">
        {flatItems.map((item) => (
          <MediaListItem key={item.id} data={item} users={users} />
        ))}
      </div>

      {flatItems.length === 0 && !isFetchingNextPage && (
        <p className="text-note-gray py-20 text-center text-sm">
          お気に入りに追加したメディアはありません
        </p>
      )}

      {/* 追加読み込み発火位置 */}
      <div ref={loadMoreRef} className="h-1" />
      {isFetchingNextPage && (
        <p className="text-note-gray py-4 text-center text-sm">読み込み中...</p>
      )}

      {/* エラーメッセージ */}
      {isError && (
        <p className="text-warning py-4 text-center text-sm dark:font-medium">
          {error?.message ?? "読み込みに失敗しました"}
        </p>
      )}
    </>
  );
};
