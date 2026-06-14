import { AlbumMediaDetail } from "@/features/album";
import { getCurrentUser } from "@/features/auth";
import { getUsers } from "@/features/auth/actions/getUsers";
import { ShareGroupMediaDetail } from "@/features/sharing";
import { getSharingGroups } from "@/features/sharing/api/getSharingGroups";
import { TagMediaDetail } from "@/features/tag";
import { getTags } from "@/features/tag/server";

import { getMedia } from "../../api/getMedia";
import { getMediaComments } from "../../api/getMediaComments";

import { MediaComment } from "./MediaComment";
import { MediaMetaData } from "./MediaMetaData";
import { MediaViewer } from "./MediaViewer";

type Props = {
  id: string;
  isModal?: boolean;
};

export const MediaDetailContent = async ({ id, isModal = false }: Props) => {
  const [media, comments, currentUser, sharingGroups, users, tags] = await Promise.all([
    getMedia(Number(id)),
    getMediaComments(Number(id)),
    getCurrentUser(),
    getSharingGroups(),
    getUsers(),
    getTags(),
  ]);

  return (
    <div
      className={`mx-auto max-w-280 px-5 pt-20 max-md:px-0 ${isModal ? "max-md:pt-0" : "max-md:pt-10"}`}
    >
      <p className="hidden">Media ID: {id}</p>
      <div className="flex gap-10 max-lg:mx-auto max-lg:max-w-150 max-lg:flex-col max-lg:items-center">
        {/* 画像、動画表示 */}
        <MediaViewer isModal={isModal} media={media} users={users} />

        {/* 詳細情報 */}
        <div className={`w-full max-md:px-5 ${isModal ? "pb-20" : ""}`}>
          {/* コメント */}
          <MediaComment
            mediaId={media.id}
            comments={comments}
            currentUser={currentUser}
            users={users}
          />

          {/* メタデータ */}
          <MediaMetaData media={media} users={users} />

          {/* タグ */}
          <TagMediaDetail mediaId={media.id} mediaTags={media.tags} tags={tags} />

          {/* 共有範囲 */}
          <ShareGroupMediaDetail media={media} sharingGroups={sharingGroups} users={users} />

          {/* アルバム */}
          <AlbumMediaDetail />
        </div>
      </div>
    </div>
  );
};
