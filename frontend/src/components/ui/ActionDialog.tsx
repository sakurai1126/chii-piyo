"use client";

import Image from "next/image";

type Props = {
  children: React.ReactNode;
  onClose?: () => void;
};

export const ActionDialog = ({ children, onClose }: Props) => {
  return (
    <div className="grid h-screen w-screen place-content-center">
      <div className="bg-background-light border-brown-dark mx-auto min-h-75 w-[calc(100vw-40px)] max-w-175 rounded-lg border p-10 max-md:min-h-0 max-md:p-5 max-md:pb-9">
        <button
          className="relative z-1 ml-auto block w-fit cursor-pointer transition-all hover:opacity-70"
          onClick={onClose}
        >
          <Image src="/images/modal-close.svg" alt="" width={12} height={12} />
        </button>
        {children}
      </div>
    </div>
  );
};
