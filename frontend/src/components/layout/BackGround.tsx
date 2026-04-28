import Image from "next/image";

export default function BackGround({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <div className="relative overflow-hidden">
      <div className="max-w-250 w-full mx-auto relative pointer-events-none ">
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

      <Image
        src="/images/bg-star.svg"
        alt=""
        width={2498}
        height={2498}
        className="absolute w-[174vw] max-w-[174vw] top-[-48vw] left-[-40vw] pointer-events-none"
      />
      <div className="relative z-1">{children}</div>
    </div>
  );
}
