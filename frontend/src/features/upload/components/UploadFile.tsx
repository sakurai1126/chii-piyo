import Image from "next/image";
import { useId, useState } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { DatePicker } from "@/components/ui/DatePicker";
import { AlbumSelector } from "@/features/album";
import { SharingSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";

import boxArrow from "../assets/brown-arrow.svg";

export const UploadFile = () => {
  const uid = useId();
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="bg-white-back border-brown-dark rounded-xl border px-5 pt-5">
      <div className="flex items-start gap-8 max-md:gap-3">
        <Image src="/images/mock-img.jpg" alt="" width={120} height={120} className="rounded-lg" />
        <div className="w-full">
          <div className="flex h-30 items-start justify-between max-md:flex-col">
            <div>
              <p className="max-md:text-[13px]">IMG_0001.jpg</p>
              <p className="mt-2 text-[13px] max-md:text-[11px]">4.2MB 3024 × 4032</p>
            </div>
            <button className="text-warning text-xs underline max-md:ml-auto">
              この画像を削除する
            </button>
          </div>
        </div>
      </div>

      <AccordionContent isOpen={isOpen} id={`accordion-${uid}`}>
        <div className="border-t-line-gray ml-37.5 border-t border-dashed pb-3 max-md:mt-5 max-md:ml-0">
          {/* コメント */}
          <p className="mt-6 max-md:text-[13px]">コメント</p>
          <textarea
            name={`comment-${uid}`}
            className="border-line-gray mt-2 h-20 w-full max-w-172.5 rounded-sm border bg-white max-md:h-18"
          />
          {/* アルバムと日付設定 */}
          <div className="mt-8 flex gap-8 max-lg:flex-col max-md:mt-4 max-md:gap-4">
            <AlbumSelector />
            <DatePicker />
          </div>
          {/* タグを編集 */}
          <TagSelector />
          {/* 共有範囲を編集 */}
          <SharingSelector />
        </div>
      </AccordionContent>

      <button
        className="mx-auto grid h-10 w-full cursor-pointer place-content-center"
        aria-expanded={isOpen}
        aria-controls={`accordion-${uid}`}
        onClick={() => setIsOpen(!isOpen)}
      >
        <Image
          src={boxArrow}
          alt=""
          width={13}
          height={7}
          className={`${isOpen ? "rotate-180" : ""} transition-transform duration-300`}
        />
      </button>
    </div>
  );
};
