import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import { PageTitle } from "@/components/ui/PageTitle";
import { isAdminUser, isEasyMode } from "@/features/auth";
import { NewRecords, RecordItem } from "@/features/record";
import { getWordRecords } from "@/features/record/server";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";
import { cn } from "@/utils/cn";

export default async function WordRecordsPage() {
  const [isAdmin, isEasy, tags, sharingGroups, wordRecords] = await Promise.all([
    isAdminUser(),
    isEasyMode(),
    getTags(),
    getSharingGroups(),
    getWordRecords(),
  ]);

  return (
    <Container className={cn("mt-10 @max-md:mt-5", isEasy && "px-5")}>
      <ChildCareNavigation currentPage="word" />
      <div className="mt-10">
        <PageTitle isEasy={isEasy} text="ことばの記録" />
        <div className="mt-15 flex items-center gap-10 @max-md:mt-5 @max-md:flex-col @max-md:gap-5">
          <div
            className={cn(
              "bg-background-normal dark:bg-background-accent border-brown-dark grid h-39 w-80 shrink-0 place-content-center rounded-lg border text-center @max-md:h-30",
              isAdmin ? "w-55" : "mx-auto",
            )}
          >
            <p className="text-6xl font-medium @max-md:text-4xl">{wordRecords.length}</p>
            <p className="mt-2">おぼえたことばの数</p>
          </div>
          {isAdmin && !isEasy && (
            <NewRecords tags={tags} sharingGroups={sharingGroups} variant="word" />
          )}
        </div>
        <div className="mt-10 @max-md:mt-5">
          {wordRecords?.map((item, index) => (
            <RecordItem
              isAdmin={isAdmin}
              isEasy={isEasy}
              tags={tags}
              sharingGroups={sharingGroups}
              key={item.id}
              item={item}
              index={index}
              variant="word"
            />
          ))}
        </div>
      </div>
    </Container>
  );
}
