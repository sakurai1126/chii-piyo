import Image from "next/image";
import Link from "next/link";

export default function NotFound() {
  return (
    <>
      <Image
        src="/images/not-found.png"
        alt=""
        width={230}
        height={146}
        className="mx-auto mt-20 @max-md:mt-15 @max-md:w-40"
      />
      <div className="mt-8 flex items-center justify-center gap-8 @max-md:mt-5 @max-md:flex-col @max-md:gap-3">
        <h1 className="text-6xl font-medium @max-md:text-[40px]">404</h1>
        <p className="font-medium @max-md:text-[13px]">お探しのページが見つかりませんでした。</p>
      </div>
      <Link
        href="/"
        className="bg-brown-light border-brown-middle hover:text-brown-dark hover:bg-white-back mx-auto mt-8 grid h-12 w-60 place-content-center rounded-lg border font-medium text-white transition-all @max-md:mt-5 @max-md:h-9 @max-md:w-45 @max-md:text-sm"
      >
        TOPに戻る
      </Link>
    </>
  );
}
