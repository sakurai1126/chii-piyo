import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { TrashContent, TrashInfo } from "@/features/trash";

export default function TrashPage() {
  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="ゴミ箱" />
      <TrashInfo />
      <TrashContent />
    </Container>
  );
}
