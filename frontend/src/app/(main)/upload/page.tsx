import { Container } from "@/components/layout/Container";
import { PageTitle } from "@/components/ui/PageTitle";
import { getAlbums } from "@/features/album/server";
import { isAdminUser, isEasyMode } from "@/features/auth/server";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";
import { UploadPageContents } from "@/features/upload";
import { cn } from "@/utils/cn";

export default async function UploadPage() {
  const [isAdmin, isEasy, albums, sharingGroups, tags] = await Promise.all([
    isAdminUser(),
    isEasyMode(),
    getAlbums(),
    getSharingGroups(),
    getTags(),
  ]);
  return (
    <Container className={cn("mt-20 @max-md:mt-5", isEasy && "px-5")}>
      <PageTitle isEasy={isEasy} text="アップロード" />
      <UploadPageContents
        isAdmin={isAdmin}
        isEasy={isEasy}
        albums={albums}
        sharingGroups={sharingGroups}
        tags={tags}
      />
    </Container>
  );
}
