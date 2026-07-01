import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { FirstRecordItem, NewRecords } from "@/features/record";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

export default async function FirstRecordsPage() {
  const [tags, sharingGroups] = await Promise.all([getTags(), getSharingGroups()]);

  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="first" />
      <div className="mt-10">
        <PageTitle text="はじめて一覧" />
        <NewRecords tags={tags} sharingGroups={sharingGroups} />
        <div className="mt-10 max-md:mt-5">
          {[1, 2, 3, 4, 5].map((item, index) => (
            <FirstRecordItem key={item} index={index} />
          ))}
        </div>
      </div>
    </Container>
  );
}
