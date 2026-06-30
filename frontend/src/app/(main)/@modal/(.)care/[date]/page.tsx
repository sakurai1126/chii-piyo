import { Modal } from "@/components/layout/Modal";
import { CareTimeLine } from "@/features/care/components/CareTimeLine";

type Props = {
  params: Promise<{ date: string }>;
};

export default async function CareDateModal({ params }: Readonly<Props>) {
  const paramsData = await params;
  const date = paramsData.date;

  return (
    <Modal>
      <CareTimeLine date={date} />
    </Modal>
  );
}
