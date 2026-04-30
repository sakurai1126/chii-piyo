import Image from "next/image";
import Link from "next/link";

import HamburgerBtn from "./HamburgerBtn";
import HamburgerMenu from "./HamburgerMenu";

export default function Header() {
  return (
    <header className="flex items-center justify-between pt-4 pr-12 max-md:h-17.5">
      <Link
        href="/"
        className="max-md:absolute max-md:top-2.5 max-md:right-0 max-md:left-0 max-md:mx-auto max-md:w-fit"
      >
        <Image
          src="/images/logo.png"
          alt="Chii-Piyo"
          width={300}
          height={100}
          className="max-md:w-45"
        ></Image>
      </Link>
      <HamburgerBtn>
        <HamburgerMenu />
      </HamburgerBtn>
    </header>
  );
}
