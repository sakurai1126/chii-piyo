"use client";

import { useInfiniteQuery } from "@tanstack/react-query";

import { GetMediaListMediaKindEnum, MediaListResponseDto } from "@/lib/api-client/gen";

// 1ページあたりの取得件数
const LIMIT = 12;

export type UseInfiniteMediaListParams = {
  mediaKind?: GetMediaListMediaKindEnum;
  albumId?: number;
  excludeAlbumId?: number;
  tagId?: number[];
  sharingGroupId?: number;
  startDate?: Date;
  endDate?: Date;
  isFavorite?: boolean;
};

type Props = {
  params?: UseInfiniteMediaListParams;
  initialData?: MediaListResponseDto;
};

/**
 * 無限スクロールでメディアリストを取得するカスタムフック
 *
 * @param params
 * メディアリスト取得のためのパラメータ
 *
 * @param initialData
 * 初期データ
 *
 * @returns
 * 無限スクロール用のメディアリストデータとフェッチ関数
 */
export const useInfiniteMediaList = ({ params = {}, initialData }: Props) => {
  // useInfiniteQueryを使用して、無限スクロールでメディアリストを取得する
  // data: フェッチされたデータ(全ページ累積分)
  // fetchNextPage: 次のページをフェッチするための関数
  // hasNextPage: 次のページが存在するかどうかのフラグ
  // isFetchingNextPage: 次のページをフェッチ中かどうかのフラグ
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, isError, error } = useInfiniteQuery(
    {
      // queryKey = クエリの一意なキー、キャッシュや再フェッチの管理に使用される。
      // media,listでメディア一覧のクエリであることを示し、検索条件が変わった時の動的変化にparamsを指定
      queryKey: ["media", "list", params],

      // queryFn = 実際にAPIからデータを取得する関数
      // pageParam = 次のページの開始位置を示すパラメータ
      // 初期値は後述のinitialPageParamで、次のページがある場合はgetNextPageParamで計算された前回までのページ数×LIMIT
      queryFn: async ({ pageParam }) => {
        const sp = new URLSearchParams();
        // 受け取った開始位置と固定追加件数をクエリパラメータに設定する
        sp.set("offset", String(pageParam));
        sp.set("limit", String(LIMIT));

        // その他のフィルタリングパラメータがある場合追加でクエリパラメータに設定する
        if (params.mediaKind) sp.set("mediaKind", params.mediaKind);
        if (params.albumId !== undefined) sp.set("albumId", String(params.albumId));
        if (params.excludeAlbumId !== undefined)
          sp.set("excludeAlbumId", String(params.excludeAlbumId));
        if (params.tagId && params.tagId.length > 0)
          params.tagId.forEach((id) => sp.append("tagId", String(id)));
        if (params.sharingGroupId !== undefined)
          sp.set("sharingGroupId", String(params.sharingGroupId));
        // 日付はISO形式の文字列(YYYY-MM-DD)に変換してクエリパラメータに設定する
        if (params.startDate) sp.set("startDate", params.startDate.toISOString().slice(0, 10));
        if (params.endDate) sp.set("endDate", params.endDate.toISOString().slice(0, 10));
        if (params.isFavorite) sp.set("isFavorite", String(true));

        // APIエンドポイントにクエリパラメータを付与してリクエストを送る
        const res = await fetch(`/api/media?${sp.toString()}`);
        if (!res.ok) {
          const body = await res.json().catch(() => ({}));
          throw new Error(body.error ?? "メディアの取得に失敗しました");
        }

        // レスポンスをMediaListResponseDto型として固定
        return res.json() as Promise<MediaListResponseDto>;
      },

      // initialPageParam = 初期開始位置
      initialPageParam: 0,

      // getNextPageParam = 次のページの開始位置を計算する関数
      // lastPage = 直前のページのデータ
      // allPages = これまでにフェッチされた全ページのデータ
      // 直前のページのhasNextがtrueなら次の開始位置は現在のページ数×LIMIT
      // hasNext=falseの場合は次のページがないと認識させるためundefinedを返す
      getNextPageParam: (lastPage, allPages) =>
        lastPage.hasNext ? allPages.length * LIMIT : undefined,
      // initialData = 初期データ
      // 引数で受け取っている場合はセットし初期ページパラメータも0に設定
      initialData: initialData ? { pages: [initialData], pageParams: [0] } : undefined,
    },
  );

  return { data, fetchNextPage, hasNextPage, isFetchingNextPage, isError, error };
};
