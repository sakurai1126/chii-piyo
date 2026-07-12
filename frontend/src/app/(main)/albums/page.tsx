import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { AlbumsGrid, AlbumsNew } from "@/features/album";
import { getAlbums } from "@/features/album/server";
import { isAdminUser } from "@/features/auth";

export default async function AlbumsPage() {
  const [isAdmin, albums] = await Promise.all([isAdminUser(), getAlbums()]);
  return (
    <Container className="mt-20 @max-md:mt-5">
      <div className="flex items-center justify-between">
        <div>
          <PageTitle text="アルバム一覧" />
        </div>
        {isAdmin && <AlbumsNew />}
      </div>
      <AlbumsGrid albums={albums} />
    </Container>
  );
}
