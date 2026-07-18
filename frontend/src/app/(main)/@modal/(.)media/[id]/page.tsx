import { Modal } from "@/components/layout/Modal";
import { MediaDetailContent } from "@/features/media/server";

type Props = {
  params: Promise<{ id: string }>;
};

export default async function MediaDetailModal({ params }: Readonly<Props>) {
  const paramsData = await params;
  const id = paramsData.id;

  return Number.isNaN(Number(id)) ? null : (
    <div className="relative z-100">
      <Modal>
        <MediaDetailContent id={id} isModal={true} />
      </Modal>
    </div>
  );
}
