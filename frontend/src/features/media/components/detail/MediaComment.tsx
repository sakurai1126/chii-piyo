import Image from "next/image";

import { Button } from "@/components/ui/Button";

export const MediaComment = () => {
  return (
    <>
      <p className="max-md:text-sm">コメント</p>
      <div className="border-brown-dark mt-2 rounded-lg border bg-[rgba(255,255,255,0.5)] px-4 py-6 backdrop-blur-[7.5px]">
        <div className="grid gap-6">
          {[1, 2, 3].map((item) => (
            <div className="flex items-start justify-between max-md:flex-col" key={item}>
              <div className="flex items-start gap-4">
                <div className="h-8 w-8 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                  <Image
                    src="/images/mock-img.jpg"
                    alt=""
                    width={31}
                    height={31}
                    className="h-full w-full rounded-full object-cover"
                  />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <p className="text-sm max-md:text-[13px]">ユーザー名</p>
                    <p className="text-xs text-gray-500 max-md:text-[11px]">2026年1月1日 12:00</p>
                  </div>
                  <p className="mt-2 text-sm max-md:mt-1 max-md:text-xs">コメント本文</p>
                </div>
              </div>
              <button className="text-warning text-xs underline max-md:mt-2 max-md:ml-auto max-md:text-[10px]">
                コメントを削除する
              </button>
            </div>
          ))}
        </div>
        {/* 新規コメント */}
        <div className="mt-10 flex items-start gap-4">
          <div className="h-8 w-8 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
            <Image
              src="/images/mock-img.jpg"
              alt=""
              width={31}
              height={31}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
          <textarea
            placeholder="コメントを入力してください"
            className="border-line-gray min-h-20 w-full rounded-sm border bg-white p-2 text-sm max-md:text-xs"
          ></textarea>
        </div>
        <Button className="mt-4 ml-auto block max-md:h-9 max-md:w-28 max-md:text-xs">
          コメントする
        </Button>
      </div>
    </>
  );
};
