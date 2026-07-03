import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { FirstRecordItem, NewRecords } from "@/features/record";
import { getFirstRecords } from "@/features/record/actions/getFirstRecords";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

export default async function FirstRecordsPage() {
  const [tags, sharingGroups, firstRecords] = await Promise.all([
    getTags(),
    getSharingGroups(),
    getFirstRecords(),
  ]);

  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="first" />
      <div className="mt-10">
        <PageTitle text="はじめて一覧" />
        <NewRecords tags={tags} sharingGroups={sharingGroups} />
        <div className="mt-10 max-md:mt-5">
          {firstRecords?.map((item, index) => (
            <FirstRecordItem
              tags={tags}
              sharingGroups={sharingGroups}
              key={item.id}
              item={item}
              index={index}
            />
          ))}
        </div>
      </div>
    </Container>
  );
}
