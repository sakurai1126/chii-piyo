import Image from "next/image";

import { Button } from "@/components/ui/Button";
import { logoutAction } from "@/features/auth/actions/logout";

import account from "../assets/account.svg";

export const Account = () => {
  return (
    <div className="mt-10 max-md:mt-8" id="account">
      <div className="flex items-center gap-2">
        <Image src={account} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        <p className="font-medium max-md:text-[13px]">アカウント</p>
      </div>
      <div className="bg-background-light border-brown-dark mt-4 rounded-lg border max-md:mt-3">
        <div className="flex items-center justify-between px-8 py-4 max-lg:px-4 max-md:px-5 max-md:py-2.5">
          <p className="max-md:text-[13px]">ログアウト</p>
          <Button variant="cancel" className="max-md:h-9 max-md:w-30" onClick={logoutAction}>
            ログアウト
          </Button>
        </div>
      </div>
    </div>
  );
};
