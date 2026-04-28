import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import BottomNavigation from "@/components/layout/BottomNavigation";
import BackGround from "@/components/layout/BackGround";
import Image from "next/image";
import Link from "next/link";

export default function NotFound() {
  return (
    <BackGround>
      <Header />

      <Image
        src="/images/not-found.png"
        alt=""
        width={230}
        height={146}
        className="mx-auto mt-20 max-md:w-40 max-md:mt-15"
      />
      <div className="flex items-center justify-center mt-8 gap-8 max-md:flex-col max-md:gap-3 max-md:mt-5">
        <h1 className="text-6xl font-medium max-md:text-[40px]">404</h1>
        <p className="font-medium max-md:text-[13px]">お探しのページが見つかりませんでした。</p>
      </div>
      <Link
        href="/"
        className="bg-brown-light text-white w-60 h-12 grid place-content-center mx-auto mt-8 rounded-lg border-brown-middle border transition-all hover:bg-white hover:text-brown-dark max-md:text-sm max-md:w-45 max-md:h-9 max-md:mt-5"
      >
        TOPに戻る
      </Link>

      <BottomNavigation />
      <Footer />
    </BackGround>
  );
}
