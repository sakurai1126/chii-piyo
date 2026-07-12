import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { isAdminUser } from "@/features/auth";
import { NewRecords, RecordItem } from "@/features/record";
import { getWordRecords } from "@/features/record/api/getWordRecords";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

export default async function WordRecordsPage() {
  const [isAdmin, tags, sharingGroups, wordRecords] = await Promise.all([
    isAdminUser(),
    getTags(),
    getSharingGroups(),
    getWordRecords(),
  ]);

  return (
    <Container className="mt-10 @max-md:mt-5">
      <ChildCareNavigation currentPage="word" />
      <div className="mt-10">
        <PageTitle text="ことばの記録" />
        <div className="mt-15 flex items-center gap-10">
          <div
            className={`bg-background-normal dark:bg-background-accent border-brown-dark grid h-39 shrink-0 place-content-center rounded-lg border text-center ${isAdmin ? "w-55" : "mx-auto w-80"}`}
          >
            <p className="text-6xl font-medium">{wordRecords.length}</p>
            <p className="mt-2">おぼえたことばの数</p>
          </div>
          {isAdmin && <NewRecords tags={tags} sharingGroups={sharingGroups} variant="word" />}
        </div>
        <div className="mt-10 @max-md:mt-5">
          {wordRecords?.map((item, index) => (
            <RecordItem
              isAdmin={isAdmin}
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
