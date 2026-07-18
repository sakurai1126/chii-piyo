import Container from "@/components/layout/Container";
import { PageTitle } from "@/components/ui/PageTitle";
import { AddMediaAlbum } from "@/features/album";
import { getAlbum } from "@/features/album/server";
import { isAdminUser, isEasyMode } from "@/features/auth";
import { getUsers } from "@/features/auth/server";
import { MediaFilter, MediaListSection } from "@/features/media";
import { getMediaList } from "@/features/media/server";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

type Props = {
  params: Promise<{ id: string }>;
};

export default async function AlbumDetailPage({ params }: Readonly<Props>) {
  const { id } = await params;
  const [isAdmin, isEasy, initialData, users, tags, sharingGroups, album] = await Promise.all([
    isAdminUser(),
    isEasyMode(),
    getMediaList({ offset: 0, limit: 12, albumId: Number(id) }),
    getUsers(),
    getTags(),
    getSharingGroups(),
    getAlbum({ albumId: Number(id) }),
  ]);

  return (
    <Container className="mt-20 @max-md:mt-5">
      <PageTitle isEasy={isEasy} text={`アルバム - ${album.title}`} />

      {initialData.totalCount > 0 ? (
        <>
          {/* 絞り込みUI */}
          <MediaFilter isEasy={isEasy} tags={tags} sharingGroups={sharingGroups} />

          {/* メディア追加UI */}
          <AddMediaAlbum
            isEasy={isEasy}
            tags={tags}
            sharingGroups={sharingGroups}
            albumId={Number(id)}
          />

          {/* 一括編集UI+メディアグリッド */}
          <MediaListSection
            isAdmin={isAdmin}
            isEasy={isEasy}
            initialData={initialData}
            users={users}
            tags={tags}
            sharingGroups={sharingGroups}
            albumId={Number(id)}
          />
        </>
      ) : (
        <>
          {/* メディア追加UI */}
          <AddMediaAlbum
            isEasy={isEasy}
            tags={tags}
            sharingGroups={sharingGroups}
            albumId={Number(id)}
          />
          <p className="py-20 text-center font-medium @max-md:text-sm">
            アルバムにメディアが追加されていません
          </p>
        </>
      )}
    </Container>
  );
}
