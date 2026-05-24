import { MediaDetailContent } from "@/features/media";

type Props = {
  params: Promise<{ id: string }>;
};

export default async function MediaDetailPage({ params }: Readonly<Props>) {
  const { id } = await params;
  return <MediaDetailContent id={id} isModal={false} />;
}
