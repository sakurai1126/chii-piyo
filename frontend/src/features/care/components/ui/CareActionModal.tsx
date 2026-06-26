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
  note: string;
  setNote: Dispatch<SetStateAction<string>>;
  saveAction: () => void;
  isPending: boolean;
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
  note,
  setNote,
  saveAction,
  isPending,
  children,
}: Props) => {
  return (
    <div className="relative z-100">
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={isPending ? undefined : onCancel}>
              <div className="flex items-start gap-10 max-md:flex-col max-md:gap-4">
                <Image src={icon} alt="" className="max-md:mx-auto" width={100} height={100} />
                <div className="w-full pr-10 max-md:pr-0">
                  <p className="font-medium max-md:text-center">{title}</p>
                  {children}
                  <input
                    type="text"
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                    className="border-line-gray focus:outline-brown-light mt-2 h-10 w-full rounded-sm border bg-white px-2"
                    disabled={isPending}
                  />
                  <SetDateAndTime
                    date={date}
                    time={time}
                    setDate={setDate}
                    setTime={setTime}
                    isPending={isPending}
                  />
                </div>
              </div>
              <ModalButtons onCancel={onCancel} saveAction={saveAction} isPending={isPending} />
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
