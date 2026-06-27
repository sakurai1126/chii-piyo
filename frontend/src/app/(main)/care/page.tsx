import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import { CareActionMenu, CareCalendar } from "@/features/care";
import { getCareRecords } from "@/features/care/api/getCareRecords";

export default async function CarePage() {
  // 今日の日付を取得
  const today = new Date();
  // 週の始まりと終わりを取得
  const startDate = new Date(
    today.getFullYear(),
    today.getMonth(),
    today.getDate() - today.getDay(),
  );
  const endDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + 7);

  // 初期データを取得
  const careRecords = await getCareRecords({ startDate, endDate });
  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="care" />
      <CareActionMenu />
      <CareCalendar initialCareRecords={careRecords} />
    </Container>
  );
}
