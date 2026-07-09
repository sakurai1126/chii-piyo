import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { isAdminUser } from "@/features/auth";
import { NewRecords, RecordItem } from "@/features/record";
import { getFirstRecords } from "@/features/record/api/getFirstRecords";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

export default async function FirstRecordsPage() {
  const [isAdmin, tags, sharingGroups, firstRecords] = await Promise.all([
    isAdminUser(),
    getTags(),
    getSharingGroups(),
    getFirstRecords(),
  ]);

  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="first" />
      <div className="mt-10">
        <PageTitle text="はじめて記録" />
        {isAdmin && (
          <div className="mt-12">
            <NewRecords tags={tags} sharingGroups={sharingGroups} variant="first" />
          </div>
        )}

        <div className="mt-10 max-md:mt-5">
          {firstRecords?.map((item, index) => (
            <RecordItem
              isAdmin={isAdmin}
              tags={tags}
              sharingGroups={sharingGroups}
              key={item.id}
              item={item}
              index={index}
              variant="first"
            />
          ))}
        </div>
      </div>
    </Container>
  );
}
