"use client";

import { useQueryClient } from "@tanstack/react-query";
import { AnimatePresence } from "motion/react";
import Image from "next/image";
import React, { useId, useState, useTransition } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";
import { Button } from "@/components/ui/Button";
import { toast } from "@/components/ui/Toast";
import { addAlbumMediaAction } from "@/features/album/";
import { DateRangeFilter } from "@/features/media/components/list/DateRangeFilter";
import { MediaKindFilter } from "@/features/media/components/list/MediaKindFilter";
import { useInfiniteMediaList } from "@/features/media/hooks/useInfiniteMediaList";
import { SharingGroupFilter } from "@/features/sharing";
import { TagFilter } from "@/features/tag";
import { useIntersectionObserver } from "@/hooks/useIntersectionObserver";
import {
  GetMediaListMediaKindEnum,
  SharingGroupResponseDto,
  TagResponseDto,
} from "@/lib/api-client/gen";

import videoIcon from "../assets/video-icon.svg";

type Props = {
  tags: TagResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  isOpen: boolean;
  setIsOpen: (value: boolean) => void;
  albumId?: number;
};

export const AddMediaModal = ({ tags, sharingGroups, isOpen, setIsOpen, albumId }: Props) => {
  const uid = useId();
  // 状態の定義
  const [selectedMediaIds, setSelectedMediaIds] = useState<number[]>([]);
  const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();
  const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false);

  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();

  // フィルター状態の管理
  type Params = {
    mediaKind: string;
    sharingGroupId: string;
    tagId: string[];
    startDate: string;
    endDate: string;
  };

  const initialFilters = {
    mediaKind: "",
    sharingGroupId: "",
    tagId: [],
    startDate: "",
    endDate: "",
  };

  const [filters, setFilters] = useState<Params>(initialFilters);

  const isFiltersChanged =
    filters.mediaKind !== "" ||
    filters.sharingGroupId !== "" ||
    filters.startDate !== "" ||
    filters.endDate !== "" ||
    filters.tagId.length > 0;

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isError, error } =
    useInfiniteMediaList({
      params: {
        mediaKind: filters.mediaKind as GetMediaListMediaKindEnum,
        sharingGroupId: filters.sharingGroupId ? Number(filters.sharingGroupId) : undefined,
        tagId: filters.tagId.map(Number),
        excludeAlbumId: albumId,
        startDate: filters.startDate ? new Date(filters.startDate) : undefined,
        endDate: filters.endDate ? new Date(filters.endDate) : undefined,
      },
      initialData: undefined,
    });

  // フェッチされた全ページのデータからitemsのみを抜き取って1つの配列にまとめる
  const items = data?.pages.flatMap((page) => page.items) ?? [];

  // スクロール時追加読み込みカスタムフック、発火位置に指定するrefを受け取る
  const loadMoreRef = useIntersectionObserver({
    // コールバックで読み込む次ページの読み込み関数
    callback: fetchNextPage,
    // 次のページが存在、かつ次のページを読み込んでいないときのみ有効化
    // 加えて開いたとモーダルを開いた時に監視を再起動させる
    enabled: !!hasNextPage && !isFetchingNextPage && isOpen,
  });

  const updateFilter = ({ key, value }: { key: string; value: string }) => {
    setFilters((prev) => {
      if (key === "tagId") {
        // tagIdの場合は、既に存在すれば削除、なければ追加する
        if (prev.tagId.includes(value)) {
          return { ...prev, tagId: prev.tagId.filter((v) => v !== value) };
        } else {
          return { ...prev, tagId: [...prev.tagId, value] };
        }
      }

      // それ以外のキーの場合単一のため上書き処理
      return { ...prev, [key]: value };
    });
  };

  // メディアを選択する処理
  const addSelectedMedia = (id: number) => {
    setSelectedMediaIds((prev) => [...prev, id]);
  };

  // メディアの選択を解除する処理
  const removeSelectedMedia = (id: number) => {
    setSelectedMediaIds((prev) => prev.filter((prevId) => prevId !== id));
  };

  const confirmOpen = () => {
    if (selectedMediaIds.length === 0) {
      toast.error("メディアを選択してください");
      return;
    }

    setIsConfirmOpen(true);
  };

  const addMediaAction = () => {
    // 現在表示されているチェック済メディアIDを抽出
    if (albumId) {
      startTransition(async () => {
        const result = await addAlbumMediaAction({
          albumId: albumId,
          mediaIds: selectedMediaIds,
        });

        if (result.success) {
          queryClient.invalidateQueries({ queryKey: ["media"] });
          setSelectedMediaIds([]);
          setFilters(initialFilters);
          setIsConfirmOpen(false);
          setIsOpen(false);

          toast.success("メディアを追加しました");
        } else {
          toast.error(result.error);
        }
      });
    }
  };

  // モーダル閉じる際のリセット処理
  const modalClose = () => {
    setSelectedMediaIds([]);
    setIsOpen(false);
  };

  return (
    <div className="relative z-100">
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <div className="grid h-screen w-screen place-content-center">
              <div className="bg-white-back border-brown-dark relative mx-auto h-[85vh] w-[calc(100vw-40px)] max-w-250 overflow-y-scroll rounded-lg border p-10 max-md:min-h-0 max-md:p-5 max-md:pb-9">
                <div className="flex items-center justify-between">
                  <p className="text-xl font-medium max-md:text-[16px]">
                    アルバムにメディアを追加する
                  </p>
                  <button
                    className="block w-fit cursor-pointer transition-all hover:opacity-70"
                    onClick={modalClose}
                    disabled={isPending}
                  >
                    <Image src="/images/modal-close.svg" alt="" width={12} height={12} />
                  </button>
                </div>
                <button
                  onClick={() => setIsFilterOpen(!isFilterOpen)}
                  className="text-brown-dark border-brown-dark bg-brown-back mt-7 flex w-full items-center justify-center gap-2 rounded-lg border py-5 text-[13px] outline-0 md:hidden"
                >
                  <p>{isFilterOpen ? "絞込検索を閉じる" : "絞込検索"}</p>

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
                    {!isFilterOpen && (
                      <path
                        d="M6 3.20004V8.80004"
                        stroke="white"
                        strokeMiterlimit="10"
                        strokeLinecap="round"
                      />
                    )}
                  </svg>
                </button>

                <div
                  className={`mt-7 grid transition-all duration-400 max-md:mt-4 ${isFilterOpen ? "max-md:grid-rows-[1fr]" : "max-md:grid-rows-[0fr]"}`}
                >
                  <div className="overflow-hidden">
                    {/* 上段 */}

                    <div className="flex grid-rows-[0fr] gap-5 max-md:flex-col max-md:gap-4">
                      {/* 写真/動画 */}
                      <MediaKindFilter
                        updateFilter={updateFilter}
                        currentValue={filters.mediaKind}
                      />

                      {/* 共有範囲 */}
                      <SharingGroupFilter
                        sharingGroups={sharingGroups}
                        updateFilter={updateFilter}
                        currentValue={filters.sharingGroupId}
                      />
                    </div>

                    {/* 下段 */}
                    <div className="mt-5 flex gap-5 max-md:flex-col max-md:gap-4">
                      {/* タグ */}
                      <TagFilter
                        tags={tags}
                        updateFilter={updateFilter}
                        currentValue={filters.tagId}
                      />

                      {/* 期間 */}
                      <DateRangeFilter
                        updateFilter={updateFilter}
                        currentStartDate={filters.startDate}
                        currentEndDate={filters.endDate}
                      />
                    </div>
                  </div>
                </div>

                <div className="mt-4 flex justify-between gap-4 max-md:flex-col max-md:items-center max-md:gap-0">
                  {isFiltersChanged && (
                    <div
                      className={`grid transition-all duration-400 ${isFilterOpen ? "max-md:grid-rows-[1fr]" : "max-md:grid-rows-[0fr]"}`}
                    >
                      <div className="overflow-hidden">
                        <Button
                          variant="cancel"
                          className="w-60 max-md:mb-4"
                          onClick={() => setFilters(initialFilters)}
                          disabled={isPending}
                        >
                          検索条件をリセットする
                        </Button>
                      </div>
                    </div>
                  )}

                  <div className="ml-auto flex gap-4 max-md:mr-auto">
                    <Button variant="cancel" onClick={modalClose} disabled={isPending}>
                      閉じる
                    </Button>
                    <Button onClick={confirmOpen} disabled={isPending}>
                      追加する
                    </Button>
                  </div>
                </div>

                {/* メディアリスト */}
                <div className="mt-4 grid grid-cols-7 gap-2 max-md:mt-2 max-md:grid-cols-3 max-md:gap-0.5">
                  {items.map((item) => (
                    <React.Fragment key={item.id}>
                      <label
                        htmlFor={`${uid}-${item.id}`}
                        className="group relative aspect-square cursor-pointer overflow-hidden"
                      >
                        <input
                          type="checkbox"
                          name={`${uid}-check`}
                          id={`${uid}-${item.id}`}
                          checked={selectedMediaIds.includes(item.id)}
                          onChange={(e) =>
                            e.target.checked
                              ? addSelectedMedia(item.id)
                              : removeSelectedMedia(item.id)
                          }
                          value={item.id}
                          className="accent-accent-pink absolute top-1 left-1 z-1"
                        />
                        <Image
                          src={item.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
                          alt=""
                          width={230}
                          height={230}
                          className="absolute h-full w-full object-cover transition-all duration-500"
                        />

                        {item.mediaType === "VIDEO" && (
                          <Image
                            src={videoIcon}
                            alt=""
                            width={40}
                            height={40}
                            className="absolute top-0 right-0 bottom-0 left-0 m-auto"
                          />
                        )}
                      </label>
                    </React.Fragment>
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
                </div>
              </div>
            </div>
          </Modal>
        )}
      </AnimatePresence>
      <AnimatePresence>
        {isConfirmOpen && (
          <Modal>
            <ActionDialog onClose={() => setIsConfirmOpen(false)}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center max-md:mt-2 max-md:mb-6 max-md:text-xs">
                  選択したメディアをアルバムに追加します。
                  <br />
                  既に別のアルバムに紐づいている場合は、新しいアルバムに上書きされます。
                  <br />
                  実行してもよろしいですか？
                </p>
                <div className="flex justify-center gap-5">
                  <Button
                    variant="cancel"
                    onClick={() => setIsConfirmOpen(false)}
                    disabled={isPending}
                  >
                    キャンセル
                  </Button>
                  <Button disabled={isPending} onClick={addMediaAction}>
                    実行する
                  </Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
