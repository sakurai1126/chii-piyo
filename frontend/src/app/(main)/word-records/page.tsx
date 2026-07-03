import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { NewRecords } from "@/features/record";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

export default async function WordRecordsPage() {
  const [tags, sharingGroups] = await Promise.all([getTags(), getSharingGroups()]);

  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="word" />
      <div className="mt-10">
        <PageTitle text="ことばの記録" />
        <div className="mt-15 flex items-center gap-10">
          <div className="bg-white-back border-brown-dark grid h-39 w-55 place-content-center rounded-lg border text-center">
            <p className="text-6xl font-medium">15</p>
            <p className="mt-2">おぼえたことばの数</p>
          </div>
          <NewRecords tags={tags} sharingGroups={sharingGroups} variant="word" />
        </div>
        <div className="mt-10 max-md:mt-5"></div>
      </div>
    </Container>
  );
}
