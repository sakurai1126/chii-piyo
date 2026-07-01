"use client";

import Image from "next/image";
import { useState } from "react";

import { AccentButton } from "@/components/ui/AccentButton";
import { AccordionContent } from "@/components/ui/AccordionContent";
import { Button } from "@/components/ui/Button";

import arrow from "../assets/arrow.svg";
import mediaIcon from "../assets/media.svg";
import plusIcon from "../assets/plus.svg";

export const NewFirstRecords = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  return (
    <div className="border-brown-dark bg-white-back mt-12 rounded-lg border border-dashed max-md:mt-6">
      {/* 開くボタン */}
      <AccordionContent isOpen={!isOpen}>
        <button
          className="hover:bg-white-back bg-green-back flex h-20 w-full cursor-pointer items-center justify-center gap-3 rounded-lg transition-all"
          onClick={() => setIsOpen(true)}
        >
          <p className="text-brown-light font-medium max-md:text-[13px]">
            新しいはじめてを記録する
          </p>
          <Image src={plusIcon} alt="" width={14} height={14} />
        </button>
      </AccordionContent>

      {/* 新規追加UI */}
      <AccordionContent isOpen={isOpen}>
        <div className="px-7 pt-7 max-md:px-4 max-md:pt-5">
          <div className="flex gap-7 max-md:flex-col max-md:gap-4">
            <div className="grid gap-2">
              <p className="max-md:text-[13px]">記録内容</p>
              <input
                type="text"
                className="focus:outline-brown-light border-line-gray h-12 w-100 rounded-sm border bg-white px-2 max-md:h-9 max-md:w-full max-md:max-w-100 max-md:text-[13px]"
              />
            </div>
            <div className="grid gap-2">
              <p className="max-md:text-[13px]">日付</p>
              <input
                type="date"
                className="focus:outline-brown-light border-line-gray h-12 w-40 rounded-sm border bg-white px-2 max-md:h-9 max-md:text-[13px]"
              />
            </div>
          </div>
          <p className="mt-5 max-md:text-[13px]">コメント</p>
          <textarea className="focus:outline-brown-light border-line-gray mt-2 h-25 w-full rounded-sm border bg-white p-2 max-md:h-20 max-md:text-[13px]"></textarea>
          <div className="mt-7 flex gap-5 max-md:mt-5 max-md:flex-col">
            <AccentButton variant="button" className="shrink-0">
              <span>写真を追加</span>
              <Image src={mediaIcon} alt="" width={16} height={16} />
            </AccentButton>
            <div className="flex flex-wrap gap-3">
              {[1, 2, 3, 4].map((item) => (
                <Image
                  src="/images/mock-img.jpg"
                  alt=""
                  className="rounded-sm max-md:h-12 max-md:w-12"
                  width={80}
                  height={80}
                  key={item}
                />
              ))}
            </div>
          </div>
          <div className="mt-5 ml-auto flex w-fit gap-5 max-md:mt-7">
            <Button variant="cancel" onClick={() => setIsOpen(false)}>
              キャンセル
            </Button>
            <Button>記録する</Button>
          </div>
        </div>
        {/* 閉じるボタン */}
        <button
          className="mt-3 grid w-full cursor-pointer place-content-center p-3 pb-5"
          onClick={() => setIsOpen(false)}
        >
          <Image src={arrow} alt="" className="" width={13} height={7} />
        </button>
      </AccordionContent>
    </div>
  );
};
