import Container from "@/components/layout/Container";
import { getAlbums } from "@/features/album/server";
import { getUsers } from "@/features/auth/actions/getUsers";
import { getAndBuildGraphData } from "@/features/graph";
import { getMediaList } from "@/features/media/server";
import { TopContents, TopMedia } from "@/features/top";

export default async function TopPage() {
  const [favoriteData, mediaData, users, { careRecords, growthRecords, wordRecords }, albums] =
    await Promise.all([
      getMediaList({ offset: 0, limit: 6, isFavorite: true }),
      getMediaList({ offset: 0, limit: 6 }),
      getUsers(),
      getAndBuildGraphData(),
      getAlbums(),
    ]);

  return (
    <>
      {/* メディア */}
      <TopMedia favoriteData={favoriteData} mediaData={mediaData} users={users} />

      <Container>
        <TopContents
          careRecords={careRecords}
          growthRecords={growthRecords}
          wordRecords={wordRecords}
          albums={albums}
        />
      </Container>
    </>
  );
}
