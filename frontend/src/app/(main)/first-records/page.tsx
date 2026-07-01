import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { FirstRecordItem, NewFirstRecords } from "@/features/first";

export default async function FirstRecordsPage() {
  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="first" />
      <div className="mt-10">
        <PageTitle text="はじめて一覧" />
        <NewFirstRecords />
        <div className="mt-10 max-md:mt-5">
          {[1, 2, 3, 4, 5].map((item, index) => (
            <FirstRecordItem key={item} index={index} />
          ))}
        </div>
      </div>
    </Container>
  );
}
