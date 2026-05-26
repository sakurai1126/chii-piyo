import Image from "next/image";

import members from "../assets/members.svg";

export const Members = () => {
  return (
    <div className="mt-10 max-md:mt-8" id="members">
      <div className="flex items-center gap-2">
        <Image src={members} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        <p className="font-medium max-md:text-[13px]">メンバー一覧</p>
      </div>
      <div className="bg-white-back border-brown-dark mt-4 rounded-lg border max-md:mt-3">
        <div className="py-4 pr-5 pl-7 max-md:p-5">
          <div className="flex justify-between">
            <div className="flex items-center gap-6">
              <div className="h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <Image
                  src="/images/mock-img.jpg"
                  alt=""
                  width={80}
                  height={80}
                  className="aspect-square h-full w-full rounded-full object-cover"
                />
              </div>
              <div className="grid gap-1">
                <p className="text-sm max-md:text-[13px]">
                  表示名：
                  <br className="md:hidden" />
                  まま
                </p>
                <p className="text-sm max-md:hidden">メールアドレス：parent@example.com</p>
                <p className="text-sm max-md:hidden">閲覧可能な共有範囲：家族全員、夫婦のみ</p>
              </div>
            </div>
            <p className="text-accent-pink bg-accent-pink-back grid h-7 w-20 place-content-center rounded-3xl border text-xs font-medium">
              管理者
            </p>
          </div>
          <div className="mt-2 grid gap-1 md:hidden">
            <p className="text-xs">メールアドレス：parent@example.com</p>
            <p className="text-xs">閲覧可能な共有範囲：家族全員、夫婦のみ</p>
          </div>
        </div>
        <div className="border-brown-dark/50 border-t py-4 pr-5 pl-7 max-md:p-5">
          <div className="flex justify-between">
            <div className="flex items-center gap-6">
              <div className="h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <Image
                  src="/images/mock-img.jpg"
                  alt=""
                  width={80}
                  height={80}
                  className="aspect-square h-full w-full rounded-full object-cover"
                />
              </div>
              <div className="grid gap-1">
                <p className="text-sm max-md:text-[13px]">
                  表示名：
                  <br className="md:hidden" />
                  まま
                </p>
                <p className="text-sm max-md:hidden">メールアドレス：parent@example.com</p>
                <p className="text-sm max-md:hidden">閲覧可能な共有範囲：家族全員、夫婦のみ</p>
              </div>
            </div>
            <p className="text-brown-middle bg-accent-orange-back grid h-7 w-20 place-content-center rounded-3xl border text-xs font-medium">
              閲覧者
            </p>
          </div>
          <div className="mt-2 grid gap-1 md:hidden">
            <p className="text-xs">メールアドレス：parent@example.com</p>
            <p className="text-xs">閲覧可能な共有範囲：家族全員、夫婦のみ</p>
          </div>
        </div>
        <div className="border-brown-dark/50 border-t py-4 pr-5 pl-7 max-md:p-5">
          <div className="flex justify-between">
            <div className="flex items-center gap-6">
              <div className="h-20 w-20 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                <Image
                  src="/images/mock-img.jpg"
                  alt=""
                  width={80}
                  height={80}
                  className="aspect-square h-full w-full rounded-full object-cover"
                />
              </div>
              <div className="grid gap-1">
                <p className="text-sm max-md:text-[13px]">
                  表示名：
                  <br className="md:hidden" />
                  まま
                </p>
                <p className="text-sm max-md:hidden">メールアドレス：parent@example.com</p>
                <p className="text-sm max-md:hidden">閲覧可能な共有範囲：家族全員、夫婦のみ</p>
              </div>
            </div>
            <p className="text-brown-middle bg-accent-orange-back grid h-7 w-20 place-content-center rounded-3xl border text-xs font-medium">
              閲覧者
            </p>
          </div>
          <div className="mt-2 grid gap-1 md:hidden">
            <p className="text-xs">メールアドレス：parent@example.com</p>
            <p className="text-xs">閲覧可能な共有範囲：家族全員、夫婦のみ</p>
          </div>
        </div>
      </div>
    </div>
  );
};
