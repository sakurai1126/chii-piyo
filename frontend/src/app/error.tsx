"use client";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import BottomNavigation from "@/components/layout/BottomNavigation";
import BackGround from "@/components/layout/BackGround";
import Image from "next/image";
import Link from "next/link";

export default function ErrorPage() {
  return (
    <BackGround>
      <Header />

      <div className="flex items-center justify-center mt-8 gap-10 max-md:flex-col-reverse max-md:gap-3 max-md:mt-15">
        <h1 className="text-6xl font-medium mt-7 max-md:mt-0 max-md:text-[40px]">500</h1>
        <Image src="/images/error.png" alt="" width={230} height={146} className="max-md:w-38" />
      </div>
      <p className="font-medium text-2xl text-center mt-7 max-md:text-lg max-md:mt-3">
        Internal Server Error.
      </p>
      <p className="font-medium text-center mt-3 max-md:text-[13px] max-md:mt-1">
        何かしらのエラーが起きたようです
      </p>
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
