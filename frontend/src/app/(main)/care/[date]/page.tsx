import { notFound } from "next/navigation";

import { isAdminUser } from "@/features/auth";
import { CareTimeLine } from "@/features/care/components/CareTimeLine";

type Props = {
  params: Promise<{ date: string }>;
};

export default async function CareDatePage({ params }: Readonly<Props>) {
  const [isAdmin, paramsData] = await Promise.all([isAdminUser(), params]);

  // 管理者でなければ404表示
  if (!isAdmin) notFound();

  const date = paramsData.date;

  return <CareTimeLine date={date} />;
}
