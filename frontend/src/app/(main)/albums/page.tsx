import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { AlbumsGrid, AlbumsNew } from "@/features/album";
import { getAlbums } from "@/features/album/server";

export default async function AlbumsPage() {
  const albums = await getAlbums();

  return (
    <Container className="mt-20 max-md:mt-5">
      <div className="flex items-center justify-between">
        <div>
          <PageTitle text="アルバム一覧" />
        </div>
        <AlbumsNew />
      </div>
      <AlbumsGrid albums={albums} />
    </Container>
  );
}
