"use client";

import Image from "next/image";
import Link from "next/link";

import BackGround from "@/components/layout/BackGround";
import BottomNavigation from "@/components/layout/BottomNavigation";
import Footer from "@/components/layout/Footer";
import Header from "@/components/layout/Header";

export default function ErrorPage() {
  return (
    <BackGround>
      <Header />

      <div className="mt-8 flex items-center justify-center gap-10 max-md:mt-15 max-md:flex-col-reverse max-md:gap-3">
        <h1 className="mt-7 text-6xl font-medium max-md:mt-0 max-md:text-[40px]">500</h1>
        <Image src="/images/error.png" alt="" width={230} height={146} className="max-md:w-38" />
      </div>
      <p className="mt-7 text-center text-2xl font-medium max-md:mt-3 max-md:text-lg">
        Internal Server Error.
      </p>
      <p className="mt-3 text-center font-medium max-md:mt-1 max-md:text-[13px]">
        何かしらのエラーが起きたようです
      </p>
      <Link
        href="/"
        className="bg-brown-light border-brown-middle hover:text-brown-dark mx-auto mt-8 grid h-12 w-60 place-content-center rounded-lg border text-white transition-all hover:bg-white max-md:mt-5 max-md:h-9 max-md:w-45 max-md:text-sm"
      >
        TOPに戻る
      </Link>

      <BottomNavigation />
      <Footer />
    </BackGround>
  );
}
