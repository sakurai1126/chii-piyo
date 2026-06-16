import { AlbumResponseDto } from "@/lib/api-client/gen";

import { SettingsAlbumListItem } from "./SettingsAlbumListItem";

type Props = {
  albums: AlbumResponseDto[];
};

export const SettingsAlbums = ({ albums }: Props) => {
  return (
    <div className="bg-white-back border-brown-dark mt-4 rounded-lg border py-2 max-md:mt-3">
      {albums.length > 0 ? (
        albums.map((album, index) => (
          <SettingsAlbumListItem album={album} index={index} key={album.id} />
        ))
      ) : (
        <p className="py-4 text-center">アルバムは作成されていません</p>
      )}
    </div>
  );
};
