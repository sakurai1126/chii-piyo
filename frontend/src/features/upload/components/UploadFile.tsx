import Image from "next/image";
import { useId, useState } from "react";

import { AccordionContent } from "@/components/ui/AccordionContent";
import { DatePicker } from "@/components/ui/DatePicker";
import { AlbumSelector } from "@/features/album";
import { SharingSelector } from "@/features/sharing";
import { TagSelector } from "@/features/tag";

import boxArrow from "../assets/brown-arrow.svg";
import { UploadImage } from "../types";

type Props = {
  item: UploadImage;
  onRemove: () => void;
};

export const UploadFile = ({ item, onRemove }: Props) => {
  const uid = useId();
  const [isOpen, setIsOpen] = useState(false);
  const sizeInKB = item.file.size / 1024;
  const sizeInMB = sizeInKB / 1024;

  return (
    <div className="bg-white-back border-brown-dark rounded-xl border px-5 pt-5">
      <div className="flex items-start gap-8 max-md:gap-3">
        <Image
          src={item.previewUrl}
          alt=""
          width={120}
          height={120}
          className="h-30 w-30 rounded-lg object-cover"
          unoptimized
        />
        <div className="w-full">
          <div className="flex h-30 items-start justify-between max-md:flex-col">
            <div>
              <p className="max-md:text-[13px]">{item.file.name}</p>
              <p className="mt-2 text-[13px] max-md:text-[11px]">
                {sizeInKB > 1024 ? `${sizeInMB.toFixed(1)}MB` : `${sizeInKB.toFixed(0)}KB`}{" "}
                {item.width && item.height && `${item.width} × ${item.height}`}
              </p>
            </div>
            <button className="text-warning text-xs underline max-md:ml-auto" onClick={onRemove}>
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
            className="border-line-gray focus:outline-brown-light mt-2 h-20 w-full max-w-172.5 rounded-sm border bg-white p-3 max-md:h-18"
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
