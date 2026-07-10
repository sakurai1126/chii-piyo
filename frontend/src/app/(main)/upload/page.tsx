import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { getAlbums } from "@/features/album/server";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";
import { UploadPageContents } from "@/features/upload";

export default async function UploadPage() {
  const [albums, sharingGroups, tags] = await Promise.all([
    getAlbums(),
    getSharingGroups(),
    getTags(),
  ]);
  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="アップロード" />
      <UploadPageContents albums={albums} sharingGroups={sharingGroups} tags={tags} />
    </Container>
  );
}
