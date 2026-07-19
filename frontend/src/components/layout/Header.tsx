import Image from "next/image";
import Link from "next/link";

import { isEasyMode } from "@/features/auth/server";
import { cn } from "@/utils/cn";

import { HeaderBtn } from "./HeaderBtn";
import { HeaderMenu } from "./HeaderMenu";

export default async function Header() {
  const isEasy = await isEasyMode();
  return (
    <header
      className={cn(
        "flex items-center justify-between pt-4 @max-md:h-17.5",
        isEasy && "relative max-w-125",
      )}
    >
      <Link href="/" className="@max-md:top-2.5 @max-md:mx-auto @max-md:w-fit">
        <Image
          src="/images/logo.png"
          alt="Chii-Piyo"
          width={300}
          height={100}
          className="@max-md:w-45"
        ></Image>
      </Link>
      <HeaderBtn isEasy={isEasy}>
        <HeaderMenu />
      </HeaderBtn>
    </header>
  );
}
