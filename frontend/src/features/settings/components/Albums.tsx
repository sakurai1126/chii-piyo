import Image from "next/image";

import { SettingsAlbums } from "@/features/album/components/settings/SettingsAlbums";
import { AlbumResponseDto } from "@/lib/api-client/gen";

import albumsIcon from "../assets/albums.svg";

type Props = {
  albums: AlbumResponseDto[];
};

export const Albums = ({ albums }: Props) => {
  return (
    <div className="mt-10 max-md:mt-8" id="albums">
      <div className="flex items-center gap-2">
        <Image src={albumsIcon} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        <p className="font-medium max-md:text-[13px]">アルバムの設定</p>
      </div>
      <SettingsAlbums albums={albums} />
    </div>
  );
};
