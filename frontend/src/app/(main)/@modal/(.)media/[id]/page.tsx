import { MediaDetailContent } from "@/features/media/server";

type Props = {
  params: Promise<{ id: string }>;
};

export default async function MediaDetailModal({ params }: Readonly<Props>) {
  const paramsData = await params;
  const id = paramsData.id;

  return Number.isNaN(Number(id)) ? null : <MediaDetailContent id={id} isModal={true} />;
}
