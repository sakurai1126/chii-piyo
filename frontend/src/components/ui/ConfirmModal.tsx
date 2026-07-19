"use client";

import { AnimatePresence } from "motion/react";
import { ReactNode } from "react";

import { Modal } from "../layout/Modal";

import { ActionDialog } from "./ActionDialog";
import { Button } from "./Button";

type Props = {
  isOpen: boolean;
  isPending: boolean;
  action: () => void;
  closeAction: () => void;
  message: ReactNode;
  buttonType?: "primary" | "cancel" | "remove";
  buttonMessage: string;
};

export const ConfirmModal = ({
  isOpen,
  isPending,
  action,
  closeAction,
  message,
  buttonType = "primary",
  buttonMessage,
}: Props) => {
  return (
    <div className="relative z-100">
      <AnimatePresence>
        {isOpen && (
          <Modal>
            <ActionDialog onClose={isPending ? undefined : closeAction}>
              <div className="flex h-full flex-col justify-center">
                <p className="text-center text-xl font-medium @max-md:text-sm">確認</p>
                <p className="mt-5 mb-10 text-center @max-md:mt-2 @max-md:mb-6 @max-md:text-xs">
                  {message}
                  <br />
                  本当によろしいですか？
                </p>
                <div className="flex justify-center gap-5">
                  <Button variant="cancel" onClick={closeAction} disabled={isPending}>
                    キャンセル
                  </Button>
                  <Button disabled={isPending} onClick={action} variant={buttonType}>
                    {buttonMessage}
                  </Button>
                </div>
              </div>
            </ActionDialog>
          </Modal>
        )}
      </AnimatePresence>
    </div>
  );
};
