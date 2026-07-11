import { notFound } from "next/navigation";

import { Modal } from "@/components/layout/Modal";
import { isAdminUser } from "@/features/auth";
import { CareTimeLine } from "@/features/care/components/CareTimeLine";

type Props = {
  params: Promise<{ date: string }>;
};

export default async function CareDateModal({ params }: Readonly<Props>) {
  const [isAdmin, paramsData] = await Promise.all([isAdminUser(), params]);
  // 管理者でなければ404表示
  if (!isAdmin) notFound();

  const date = paramsData.date;

  return (
    <Modal>
      <CareTimeLine date={date} />
    </Modal>
  );
}
