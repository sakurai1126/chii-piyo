import Image from "next/image";
import Link from "next/link";

import { AlbumResponseDto } from "@/lib/api-client/gen";

import media from "../assets/media.svg";
import video from "../assets/video.svg";

import { AlbumsGridSlide } from "./AlbumsGridSlide";

type Props = {
  albums: AlbumResponseDto[];
};

export const AlbumsGrid = ({ albums }: Props) => {
  return (
    <div className="mt-15 grid grid-cols-4 gap-2 max-md:grid-cols-2">
      {albums.map((album) => (
        <Link href={`/albums/${album.id}`} className="relative" key={album.id}>
          <div className="absolute bottom-5 -left-1 z-10 max-w-full min-w-40 bg-[linear-gradient(90deg,rgba(185,0,0,0.8)_0%,rgba(185,0,0,0.64)_65%,rgba(185,0,0,0)_100%)] py-1 pr-5 pl-4 backdrop-blur-[7.5px]">
            <p className="line-clamp-3 text-xs break-all text-white max-md:text-[10px]">
              {album.title}
            </p>
          </div>
          <div className="absolute top-4 right-2 z-10 flex items-center gap-1 max-md:top-3">
            <div className="rounded-2xl bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
              <div className="bg-white-back flex items-center gap-1 rounded-2xl px-2.5 py-1">
                <Image
                  src={media}
                  alt=""
                  width={12}
                  height={12}
                  className="max-md:h-[10px] max-md:w-[10px]"
                />
                <p className="text-brown-dark text-xs max-md:text-[10px]">{album.photoCount}</p>
              </div>
            </div>
            <div className="rounded-2xl bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
              <div className="bg-white-back flex items-center gap-1 rounded-2xl px-2.5 py-1">
                <Image
                  src={video}
                  alt=""
                  width={16}
                  height={16}
                  className="max-md:h-[14px] max-md:w-[14px]"
                />
                <p className="text-brown-dark text-xs max-md:text-[10px]">{album.videoCount}</p>
              </div>
            </div>
          </div>
          {album.coverMediaUrls.length > 1 ? (
            <AlbumsGridSlide album={album} />
          ) : (
            <div className="group overflow-hidden">
              <Image
                src={album.coverMediaUrls[0] ?? "/images/no-image.svg"}
                alt=""
                className="aspect-square transition-all duration-700 group-hover:scale-110"
                width={245}
                height={245}
              />
            </div>
          )}
        </Link>
      ))}
    </div>
  );
};
