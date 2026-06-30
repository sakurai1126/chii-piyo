import { CareTimeLine } from "@/features/care/components/CareTimeLine";

type Props = {
  params: Promise<{ date: string }>;
};

export default async function CareDatePage({ params }: Readonly<Props>) {
  const paramsData = await params;
  const date = paramsData.date;

  return <CareTimeLine date={date} />;
}
