import Image from "next/image";

import { AlbumResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import arrow from "../assets/arrow.svg";

import { AlbumAddForm } from "./AlbumAddForm";

type Props = {
  isAdmin: boolean;
  isEasy?: boolean;
  albums: AlbumResponseDto[];
  onAlbumSelect: (albumId: number) => void;
  selectedAlbumId?: number;
};

export const AlbumSelector = ({
  isAdmin,
  isEasy,
  albums,
  onAlbumSelect,
  selectedAlbumId,
}: Props) => {
  return (
    <div>
      <p className={cn("@max-md:text-[13px]", isEasy && "font-medium @max-md:text-[16px]")}>
        アルバム
      </p>

      {/* アルバム選択 */}
      {albums.length > 0 ? (
        <div
          className={cn(
            "border-line-gray bg-light-dark relative mt-2 h-12 w-115 max-w-full rounded-sm border @max-md:h-9",
            isEasy && "@max-md:h-12",
          )}
        >
          <select
            className={cn(
              "focus:outline-brown-light bg-light-dark h-full w-full appearance-none px-4 @max-md:px-3 @max-md:text-[13px] dark:outline-none",
              isEasy && "font-medium @max-md:text-[16px]",
            )}
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
        <p className="mt-5 mr-10 text-sm @max-md:mt-3 @max-md:text-xs">アルバムがありません</p>
      )}

      {isAdmin && !isEasy && <AlbumAddForm />}
    </div>
  );
};
