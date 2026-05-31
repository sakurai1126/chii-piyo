import Image from "next/image";

import { Button } from "@/components/ui/Button";

import sharingGroups from "../assets/sharing-groups.svg";

export const SharingGroups = () => {
  return (
    <div className="mt-10 max-md:mt-8" id="sharing-groups">
      <div className="flex items-center gap-2">
        <Image
          src={sharingGroups}
          alt=""
          width={30}
          height={30}
          className="max-md:h-6 max-md:w-6"
        />
        <p className="font-medium max-md:text-[13px]">共有範囲の設定</p>
      </div>
      <div className="bg-white-back border-brown-dark mt-4 rounded-lg border max-md:mt-3">
        <div className="flex items-center justify-between px-8 py-4 max-lg:px-4 max-md:flex-col max-md:items-start max-md:px-5">
          <div className="flex items-center max-md:flex-col max-md:items-start">
            <p className="shrink-0 max-md:text-[13px]">家族全員</p>
            <div className="ml-10 flex flex-wrap gap-x-6 gap-y-2 max-md:mt-3 max-md:ml-0 max-md:gap-x-3">
              {[1, 2, 3, 4].map((item) => (
                <div className="flex items-center gap-2" key={item}>
                  <div className="h-10 w-10 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                    <Image
                      src="/images/mock-img.jpg"
                      alt=""
                      width={40}
                      height={40}
                      className="aspect-square h-full w-full rounded-full object-cover"
                    />
                  </div>
                  <p className="max-md:text-[13px]">まま</p>
                </div>
              ))}
            </div>
          </div>
          <div className="flex shrink-0 gap-5 max-md:mt-3 max-md:ml-auto">
            <button className="text-sm underline max-md:text-[10px]">編集</button>
            <button className="text-warning text-sm underline max-md:text-[10px]">削除</button>
          </div>
        </div>
        <div className="border-brown-dark/50 flex items-center justify-between border-t px-8 py-4 max-lg:px-4 max-md:flex-col max-md:items-start max-md:px-5">
          <div className="flex items-center max-md:flex-col max-md:items-start">
            <p className="shrink-0 max-md:text-[13px]">夫婦のみ</p>
            <div className="ml-10 flex flex-wrap gap-x-6 gap-y-2 max-md:mt-3 max-md:ml-0 max-md:gap-x-3">
              {[1, 2].map((item) => (
                <div className="flex items-center gap-2" key={item}>
                  <div className="h-10 w-10 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                    <Image
                      src="/images/mock-img.jpg"
                      alt=""
                      width={40}
                      height={40}
                      className="aspect-square h-full w-full rounded-full object-cover"
                    />
                  </div>
                  <p className="max-md:text-[13px]">まま</p>
                </div>
              ))}
            </div>
          </div>
          <div className="flex shrink-0 gap-5 max-md:mt-3 max-md:ml-auto">
            <button className="text-sm underline max-md:text-[10px]">編集</button>
            <button className="text-warning text-sm underline max-md:text-[10px]">削除</button>
          </div>
        </div>
      </div>
      <Button className="mt-5 ml-auto block max-md:mx-auto max-md:w-30">新規追加</Button>
    </div>
  );
};
