import Image from "next/image";

import { isEasyMode } from "@/features/auth";
import { cn } from "@/utils/cn";

export default async function BackGround({ children }: Readonly<{ children: React.ReactNode }>) {
  const isEasy = await isEasyMode();
  return (
    <div
      className={cn(
        "relative overflow-clip",
        isEasy &&
          "bg-[url('/images/easy-bg-light.jpg')] bg-contain bg-top dark:bg-[url('/images/easy-bg-dark.jpg')] dark:bg-cover",
      )}
    >
      <div className="pointer-events-none relative mx-auto w-full max-w-250 max-lg:max-w-100 max-md:max-w-30">
        <Image
          src="/images/bg-illust-1.png"
          alt=""
          width={126}
          height={126}
          className={cn(
            "absolute top-85 -right-60 max-md:-right-43 max-md:w-23",
            isEasy && "md:hidden",
          )}
        />

        <Image
          src="/images/bg-illust-2.png"
          alt=""
          width={155}
          height={184}
          className={cn(
            "absolute top-220 -left-60 max-md:-left-40 max-md:w-25",
            isEasy && "md:hidden",
          )}
        />

        <Image
          src="/images/bg-illust-3.png"
          alt=""
          width={98}
          height={103}
          className={cn("absolute top-340 -right-40 max-md:w-20", isEasy && "md:hidden")}
        />
      </div>

      <div className={cn("@container", isEasy && "bg-background mx-auto max-w-125")}>
        <div className="relative z-1 bg-[url('/images/bg-star.svg')] bg-contain bg-top @max-md:bg-[url('/images/bg-star-sp.svg')] dark:bg-[url('/images/bg-star-light.svg')] dark:@max-md:bg-[url('/images/bg-star-light-sp.svg')]">
          {children}
        </div>
      </div>
    </div>
  );
}
