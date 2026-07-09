import Container from "@/components/layout/Container";
import { getAlbums } from "@/features/album/server";
import { isAdminUser } from "@/features/auth";
import { getUsers } from "@/features/auth/actions/getUsers";
import { getAndBuildGraphData } from "@/features/graph";
import { getMediaList } from "@/features/media/server";
import { TopContents, TopMedia } from "@/features/top";

export default async function TopPage() {
  const isAdmin = await isAdminUser();
  const [favoriteData, mediaData, users, { careRecords, growthRecords, wordRecords }, albums] =
    await Promise.all([
      getMediaList({ offset: 0, limit: 6, isFavorite: true }),
      getMediaList({ offset: 0, limit: 6 }),
      getUsers(),
      getAndBuildGraphData(isAdmin),
      getAlbums(),
    ]);

  return (
    <>
      {/* メディア */}
      <TopMedia favoriteData={favoriteData} mediaData={mediaData} users={users} />

      <Container>
        <TopContents
          isAdmin={isAdmin}
          careRecords={careRecords}
          growthRecords={growthRecords}
          wordRecords={wordRecords}
          albums={albums}
        />
      </Container>
    </>
  );
}
