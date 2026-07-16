import { Modal } from "@/components/layout/Modal";

export default function MediaModalLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <div className="relative z-100">
      <Modal>{children}</Modal>
    </div>
  );
}
