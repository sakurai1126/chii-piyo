import Image from "next/image";
import Link from "next/link";
import HamburgerBtn from "./HamburgerBtn";
import HamburgerMenu from "./HamburgerMenu";

export default function Header() {
  return (
    <header className="flex items-center justify-between pt-4 pr-12">
      <Link href="/">
        <Image src="/images/logo.png" alt="Chii-Piyo" width={300} height={100}></Image>
      </Link>
      <HamburgerBtn>
        <HamburgerMenu />
      </HamburgerBtn>
    </header>
  );
}
