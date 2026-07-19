import Container from "@/components/layout/Container";
import { getAlbums } from "@/features/album/server";
import { isAdminUser, isEasyMode, getUsers } from "@/features/auth/server";
import { getAndBuildGraphData } from "@/features/graph";
import { getMediaList } from "@/features/media/server";
import { TopContents, TopMedia } from "@/features/top";

export default async function TopPage() {
  const isAdmin = await isAdminUser();
  const [
    isEasy,
    favoriteData,
    mediaData,
    users,
    { careRecords, growthRecords, wordRecords },
    albums,
  ] = await Promise.all([
    isEasyMode(),
    getMediaList({ offset: 0, limit: 6, isFavorite: true }),
    getMediaList({ offset: 0, limit: 6 }),
    getUsers(),
    getAndBuildGraphData(isAdmin),
    getAlbums(),
  ]);

  return (
    <>
      {/* メディア */}
      <TopMedia isEasy={isEasy} favoriteData={favoriteData} mediaData={mediaData} users={users} />

      <Container>
        <TopContents
          isAdmin={isAdmin}
          isEasy={isEasy}
          careRecords={careRecords}
          growthRecords={growthRecords}
          wordRecords={wordRecords}
          albums={albums}
        />
      </Container>
    </>
  );
}
