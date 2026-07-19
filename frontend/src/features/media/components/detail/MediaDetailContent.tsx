import { AlbumMediaDetail } from "@/features/album";
import { getAlbum } from "@/features/album/server";
import { getUsers, getCurrentUser, isAdminUser, isEasyMode } from "@/features/auth/server";
import { ShareGroupMediaDetail } from "@/features/sharing";
import { getSharingGroups } from "@/features/sharing/server";
import { TagMediaDetail } from "@/features/tag";
import { getTags } from "@/features/tag/server";
import { cn } from "@/utils/cn";

import { getMedia } from "../../api/getMedia";
import { getMediaComments } from "../../api/getMediaComments";

import { MediaComment } from "./MediaComment";
import { MediaDelete } from "./MediaDelete";
import { MediaMetaData } from "./MediaMetaData";
import { MediaViewer } from "./MediaViewer";

type Props = {
  id: string;
  isModal?: boolean;
};

export const MediaDetailContent = async ({ id, isModal = false }: Props) => {
  const [isAdmin, isEasy, media, comments, currentUser, sharingGroups, users, tags] =
    await Promise.all([
      isAdminUser(),
      isEasyMode(),
      getMedia(Number(id)),
      getMediaComments(Number(id)),
      getCurrentUser(),
      getSharingGroups(),
      getUsers(),
      getTags(),
    ]);

  const album = media.albumId ? await getAlbum({ albumId: media.albumId }) : null;

  return (
    <div
      className={cn(
        "mx-auto max-w-280 px-5 pt-20 @max-md:px-0 @max-md:pt-10",
        isModal && "@max-md:pt-0",
        isEasy && "max-w-125",
      )}
    >
      <p className="hidden">Media ID: {id}</p>
      <div className="flex gap-10 @max-lg:mx-auto @max-lg:max-w-150 @max-lg:flex-col @max-lg:items-center">
        {/* 画像、動画表示 */}
        <MediaViewer isEasy={isEasy} media={media} isModal={isModal} users={users} />

        {/* 詳細情報 */}
        <div className={cn("w-full @max-md:px-5", isModal && "pb-20")}>
          {/* コメント */}
          <MediaComment
            mediaId={media.id}
            isEasy={isEasy}
            comments={comments}
            currentUser={currentUser}
            users={users}
          />
          {!isEasy && (
            <>
              {/* メタデータ */}
              <MediaMetaData media={media} users={users} />

              {/* タグ */}
              <TagMediaDetail
                isAdmin={isAdmin}
                mediaId={media.id}
                mediaTags={media.tags}
                tags={tags}
              />

              {/* 共有範囲 */}
              <ShareGroupMediaDetail
                isAdmin={isAdmin}
                media={media}
                sharingGroups={sharingGroups}
                users={users}
              />
            </>
          )}
          {/* アルバム */}
          {album && (
            <AlbumMediaDetail isAdmin={isAdmin} isEasy={isEasy} album={album} media={media} />
          )}

          {/* メディア削除UI */}
          {isAdmin && !isEasy && <MediaDelete mediaId={media.id} />}
        </div>
      </div>
    </div>
  );
};
