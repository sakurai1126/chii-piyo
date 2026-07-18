import { notFound } from "next/navigation";

import { Modal } from "@/components/layout/Modal";
import { isAdminUser, isEasyMode } from "@/features/auth";
import { CareTimeLine } from "@/features/care";

type Props = {
  params: Promise<{ date: string }>;
};

export default async function CareDateModal({ params }: Readonly<Props>) {
  const [isAdmin, isEasy, paramsData] = await Promise.all([isAdminUser(), isEasyMode(), params]);
  // 管理者以外 or かんたんモードであれば04表示
  if (!isAdmin || isEasy) notFound();

  const date = paramsData.date;

  return (
    <Modal>
      <CareTimeLine date={date} />
    </Modal>
  );
}
