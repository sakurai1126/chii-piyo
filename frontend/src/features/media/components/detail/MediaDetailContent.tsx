import { AlbumMediaDetail } from "@/features/album";
import { ShareGroupMediaDetail } from "@/features/sharing";
import { TagMediaDetail } from "@/features/tag";

import { getMedia } from "../../api/getMedia";

import { MediaComment } from "./MediaComment";
import { MediaMetaData } from "./MediaMetaData";
import { MediaViewer } from "./MediaViewer";

type Props = {
  id: string;
  isModal?: boolean;
};

export const MediaDetailContent = async ({ id, isModal = false }: Props) => {
  const media = await getMedia(Number(id));

  return (
    <div
      className={`mx-auto max-w-280 px-5 pt-20 max-md:px-0 ${isModal ? "max-md:pt-0" : "max-md:pt-10"}`}
    >
      <p className="hidden">Media ID: {id}</p>
      <div className="flex gap-10 max-lg:mx-auto max-lg:max-w-150 max-lg:flex-col max-lg:items-center">
        {/* 画像、動画表示 */}
        <MediaViewer isModal={isModal} media={media} />

        {/* 詳細情報 */}
        <div className="w-full pb-20 max-md:px-5">
          {/* コメント */}
          <MediaComment />

          {/* メタデータ */}
          <MediaMetaData />

          {/* タグ */}
          <TagMediaDetail />

          {/* 共有範囲 */}
          <ShareGroupMediaDetail />

          {/* アルバム */}
          <AlbumMediaDetail />
        </div>
      </div>
    </div>
  );
};
