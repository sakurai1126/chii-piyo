import { Container } from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import { PageTitle } from "@/components/ui/PageTitle";
import { isAdminUser, isEasyMode } from "@/features/auth/server";
import { NewRecords, RecordItem } from "@/features/record";
import { getFirstRecords } from "@/features/record/server";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";
import { cn } from "@/utils/cn";

export default async function FirstRecordsPage() {
  const [isAdmin, isEasy, tags, sharingGroups, firstRecords] = await Promise.all([
    isAdminUser(),
    isEasyMode(),
    getTags(),
    getSharingGroups(),
    getFirstRecords(),
  ]);

  return (
    <Container className={cn("mt-10 @max-md:mt-5", isEasy && "px-5")}>
      <ChildCareNavigation currentPage="first" />
      <div className="mt-10">
        <PageTitle isEasy={isEasy} text="はじめて記録" />
        {isAdmin && !isEasy && (
          <div className="mt-12">
            <NewRecords tags={tags} sharingGroups={sharingGroups} variant="first" />
          </div>
        )}

        <div className={cn("mt-10", isAdmin && !isEasy && "@max-md:mt-5")}>
          {firstRecords?.map((item, index) => (
            <RecordItem
              isAdmin={isAdmin}
              isEasy={isEasy}
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
