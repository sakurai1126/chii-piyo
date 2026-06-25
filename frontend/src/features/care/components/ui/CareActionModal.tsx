import { AnimatePresence } from "motion/react";
import Image, { StaticImageData } from "next/image";
import { Dispatch, SetStateAction } from "react";

import { Modal } from "@/components/layout/Modal";
import { ActionDialog } from "@/components/ui/ActionDialog";

import { ModalButtons } from "./ModalButtons";
import { SetDateAndTime } from "./SetDateAndTime";

type Props = {
  title: string;
  isOpen: boolean;
  icon: StaticImageData;
  date: string;
  setDate: Dispatch<SetStateAction<string>>;
  time: string;
  setTime: Dispatch<SetStateAction<string>>;
  onCancel: () => void;
  saveAction: () => void;
  children?: React.ReactNode;
};
export const CareActionModal = ({
  title,
  isOpen,
  icon,
  date,
  setDate,
  time,
  setTime,
  onCancel,
  saveAction,
  children,
}: Props) => {
  return (
    <div className="relative z-100">
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={onCancel}>
              <div className="flex items-start gap-10 max-md:flex-col max-md:gap-4">
                <Image src={icon} alt="" className="max-md:mx-auto" width={100} height={100} />
                <div className="w-full pr-10 max-md:pr-0">
                  <p className="font-medium max-md:text-center">{title}</p>
                  {children}
                  <input
                    type="text"
                    className="border-line-gray mt-2 h-10 w-full rounded-sm border bg-white"
                  />
                  <SetDateAndTime date={date} time={time} setDate={setDate} setTime={setTime} />
                </div>
              </div>
              <ModalButtons onCancel={onCancel} saveAction={saveAction} />
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
