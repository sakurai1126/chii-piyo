import Image from "next/image";

import { Button } from "@/components/ui/Button";
import { logoutAction } from "@/features/auth/actions/logout";

import icon from "../assets/account.svg";
import whiteIcon from "../assets/white/account.svg";

type Props = {
  isEasy: boolean;
};

export const Account = ({ isEasy }: Props) => {
  return (
    <div className="mt-10 @max-md:mt-8" id="account">
      <div className="flex items-center gap-2">
        <Image
          src={icon}
          alt=""
          width={30}
          height={30}
          className="max-md:w-6 @max-md:h-6 dark:hidden"
        />
        <Image
          src={whiteIcon}
          alt=""
          width={30}
          height={30}
          className="hidden @max-md:h-6 @max-md:w-6 dark:block"
        />
        <p className={`font-medium ${isEasy ? "text-[18px]" : "@max-md:text-[13px]"}`}>
          アカウント
        </p>
      </div>
      <div className="bg-background-normal dark:bg-background-accent border-brown-dark mt-4 rounded-lg border @max-md:mt-3">
        <div className="flex items-center justify-between px-8 py-4 @max-lg:px-4 @max-md:px-5 @max-md:py-2.5">
          <p className={isEasy ? "font-medium" : "@max-md:text-[13px]"}>ログアウト</p>
          <Button variant="cancel" className="max-md:w-30 @max-md:h-9" onClick={logoutAction}>
            ログアウト
          </Button>
        </div>
      </div>
    </div>
  );
};
