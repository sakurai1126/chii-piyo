import Image from "next/image";

import { AlbumResponseDto } from "@/lib/api-client/gen";

import arrow from "../assets/arrow.svg";

import { AlbumAddForm } from "./AlbumAddForm";

type Props = {
  isAdmin: boolean;
  // 表示するアルバム一覧
  albums: AlbumResponseDto[];
  // アルバム選択時のコールバック
  onAlbumSelect: (albumId: number) => void;
  // 現在選択されているアルバムID
  selectedAlbumId?: number;
};

export const AlbumSelector = ({ isAdmin, albums, onAlbumSelect, selectedAlbumId }: Props) => {
  return (
    <div>
      <p className="max-md:text-[13px]">アルバム</p>

      {/* アルバム選択 */}
      {albums.length > 0 ? (
        <div className="border-line-gray bg-light-dark relative mt-2 h-12 w-115 max-w-full rounded-sm border max-md:h-9">
          <select
            className="focus:outline-brown-light bg-light-dark h-full w-full appearance-none px-4 max-md:px-3 max-md:text-[13px] dark:outline-none"
            onChange={(e) => onAlbumSelect(Number(e.target.value))}
            value={selectedAlbumId ?? ""}
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
      ) : (
        <p className="mt-5 mr-10 text-sm max-md:mt-3 max-md:text-xs">アルバムがありません</p>
      )}

      {isAdmin && <AlbumAddForm />}
    </div>
  );
};
