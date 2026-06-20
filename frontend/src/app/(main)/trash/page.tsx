import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { TrashContent, TrashInfo } from "@/features/trash";
import { getTrashItems } from "@/features/trash/server";

export default async function TrashPage() {
  const trashItems = await getTrashItems({ offset: 1, limit: 3 });
  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="ゴミ箱" />
      <TrashInfo trashItems={trashItems} />
      <TrashContent trashItems={trashItems} />
    </Container>
  );
}
