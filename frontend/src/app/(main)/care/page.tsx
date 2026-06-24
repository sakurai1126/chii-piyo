import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import { CareActionMenu, CareCalendar } from "@/features/care";

export default function CarePage() {
  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="care" />
      <CareActionMenu />
      <CareCalendar />
    </Container>
  );
}
