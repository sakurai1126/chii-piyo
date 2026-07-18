import Container from "@/components/layout/Container";
import { PageTitle } from "@/components/ui/PageTitle";
import { AlbumsGrid, AlbumsNew } from "@/features/album";
import { getAlbums } from "@/features/album/server";
import { isAdminUser, isEasyMode } from "@/features/auth";
import { cn } from "@/utils/cn";

export default async function AlbumsPage() {
  const [isAdmin, isEasy, albums] = await Promise.all([isAdminUser(), isEasyMode(), getAlbums()]);
  return (
    <Container className="mt-20 @max-md:mt-5">
      <div className="flex items-center justify-between">
        <div className={cn(isEasy && "mx-auto")}>
          <PageTitle isEasy={isEasy} text="アルバム一覧" />
        </div>
        {isAdmin && !isEasy && <AlbumsNew />}
      </div>

      {albums.length ? (
        <AlbumsGrid isEasy={isEasy} albums={albums} />
      ) : (
        <p className="my-15 text-center @max-md:text-sm">アルバムは作成されていません</p>
      )}
    </Container>
  );
}
