import Image from "next/image";

export default function BackGround({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <div className="relative overflow-clip">
      <div className="pointer-events-none relative mx-auto w-full max-w-250">
        <Image
          src="/images/bg-illust-1.png"
          alt=""
          width={126}
          height={126}
          className="absolute top-45 -right-60"
        />

        <Image
          src="/images/bg-illust-2.png"
          alt=""
          width={155}
          height={184}
          className="absolute top-180 -left-60"
        />

        <Image
          src="/images/bg-illust-3.png"
          alt=""
          width={98}
          height={103}
          className="absolute top-300 -right-40"
        />
      </div>

      {/* ライトモード時のみ表示 */}
      <Image
        src="/images/bg-star.svg"
        alt=""
        width={2498}
        height={2498}
        className="pointer-events-none absolute top-[-48vw] left-[-40vw] block w-[174vw] max-w-[174vw] dark:hidden"
      />

      {/* ダークモード時のみ表示 */}
      <Image
        src="/images/bg-star-light.svg"
        alt=""
        width={2498}
        height={2498}
        className="pointer-events-none absolute top-[-48vw] left-[-40vw] hidden w-[174vw] max-w-[174vw] dark:block"
      />
      <div className="relative z-1">{children}</div>
    </div>
  );
}
