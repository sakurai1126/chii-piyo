import Image from "next/image";
import Link from "next/link";

import { AlbumResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import media from "../assets/media.svg";
import video from "../assets/video.svg";

import { AlbumsGridSlide } from "./AlbumsGridSlide";

type Props = {
  isEasy: boolean;
  albums: AlbumResponseDto[];
  variant?: "top" | "page";
};

export const AlbumsGrid = ({ isEasy, albums, variant = "page" }: Props) => {
  // トップページからの呼び出しの場合albumsを4件のみの表示にする
  const displayAlbums = variant === "top" ? albums.slice(0, 4) : albums;

  return (
    <div className="mt-15 grid grid-cols-4 gap-2 @max-md:grid-cols-2">
      {displayAlbums.map((album) => (
        <div className="relative" key={album.id}>
          <div
            className={cn(
              "pointer-events-none absolute z-10 max-w-full min-w-40 py-1",
              isEasy
                ? "bg-warning-back right-0 bottom-3 left-0 mx-auto w-[calc(100%-24px)] rounded-sm px-1 text-center"
                : "bottom-5 -left-1 bg-[linear-gradient(90deg,rgba(185,0,0,0.8)_0%,rgba(185,0,0,0.64)_65%,rgba(185,0,0,0)_100%)] pr-5 pl-4 backdrop-blur-[7.5px]",
            )}
          >
            <p
              className={cn(
                "line-clamp-3 text-xs break-all text-white",
                isEasy ? "text-[13px] font-medium" : "@max-md:text-[10px]",
              )}
            >
              {album.title}
            </p>
          </div>
          {/* アルバム内メディアカウント */}
          {!isEasy && (
            <div className="pointer-events-none absolute top-4 right-2 z-10 flex items-center gap-1 @max-md:top-3">
              <div className="rounded-2xl bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <div className="bg-white-back flex items-center gap-1 rounded-2xl px-2.5 py-1">
                  <Image
                    src={media}
                    alt=""
                    width={12}
                    height={12}
                    className="max-md:w-[10px] @max-md:h-[10px]"
                  />
                  <p className="text-brown-dark text-xs @max-md:text-[10px]">{album.photoCount}</p>
                </div>
              </div>
              <div className="rounded-2xl bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <div className="bg-white-back flex items-center gap-1 rounded-2xl px-2.5 py-1">
                  <Image
                    src={video}
                    alt=""
                    width={16}
                    height={16}
                    className="max-md:w-[14px] @max-md:h-[14px]"
                  />
                  <p className="text-brown-dark text-xs @max-md:text-[10px]">{album.videoCount}</p>
                </div>
              </div>
            </div>
          )}
          {album.coverMediaUrls.length > 1 ? (
            <AlbumsGridSlide album={album} />
          ) : (
            <Link href={`/albums/${album.id}`} className="group block overflow-hidden">
              <Image
                src={album.coverMediaUrls[0] ?? "/images/no-image.svg"}
                alt=""
                className="aspect-square transition-all duration-700 group-hover:scale-110"
                width={245}
                height={245}
              />
            </Link>
          )}
        </div>
      ))}
    </div>
  );
};
