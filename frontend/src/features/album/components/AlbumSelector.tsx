import Image from "next/image";

import ReadError from "@/components/ui/ReadError";
import { AlbumResponseDto } from "@/lib/api-client/gen";

import arrow from "../assets/arrow.svg";

type Props = {
  // 表示するアルバム一覧
  albums: AlbumResponseDto[];
  // アルバム取得中フラグ
  isLoading?: boolean;
  // アルバム取得失敗時のエラーメッセージ
  error?: string | null;
  // 取得失敗時の再試行
  onRefresh?: () => void;
  // アルバム選択時のコールバック
  onAlbumSelect: (albumId: string) => void;
};

export const AlbumSelector = ({
  albums,
  isLoading = false,
  error = null,
  onRefresh,
  onAlbumSelect,
}: Props) => {
  return (
    <div>
      <p className="max-md:text-[13px]">アルバム</p>

      {/* エラー時は再試行ボタンを表示 */}
      {!isLoading && error && <ReadError error={error} onRefresh={onRefresh} />}
      {/* 読み込み完了後 */}
      {!isLoading && !error && (
        <>
          {/* アルバム0件の表示 */}
          {albums.length === 0 && (
            <p className="mt-5 mr-10 text-sm max-md:mt-3 max-md:text-xs">アルバムがありません</p>
          )}

          {/* アルバム選択 */}
          {albums.length > 0 && (
            <div className="border-line-gray relative mt-2 h-12 w-115 max-w-full rounded-sm border bg-white max-md:h-9">
              <select
                name=""
                className="focus:outline-brown-light h-full w-full appearance-none px-4 max-md:px-3 max-md:text-[13px]"
                onChange={(e) => onAlbumSelect(e.target.value)}
              >
                <option value="">選択してください</option>
                {albums.map((album) => (
                  <option key={album.id} value={album.id}>
                    {album.title}
                  </option>
                ))}
              </select>
              <Image
                src={arrow}
                alt=""
                width={13}
                height={7}
                className="pointer-events-none absolute top-0 right-5 bottom-0 my-auto"
              />
            </div>
          )}
        </>
      )}
    </div>
  );
};
