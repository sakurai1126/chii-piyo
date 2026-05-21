"use client";

import Image from "next/image";
import Link from "next/link";

import { useModalClose } from "@/components/layout/Modal";
import { FavoriteMediaDetail } from "@/features/favorite";

import download from "../../assets/download.svg";
import leftArrow from "../../assets/left-arrow.svg";
import rightArrow from "../../assets/right-arrow.svg";
import zoom from "../../assets/zoom.svg";

type Props = {
  isModal?: boolean;
};

export const MediaViewer = ({ isModal }: Props) => {
  const handleClose = useModalClose();

  return (
    <div className="w-125 shrink-0 max-xl:w-110 max-md:w-full">
      <div className="relative">
        {/* 画像、動画表示 */}
        <Image
          src="/images/mock-img.jpg"
          alt=""
          width={500}
          height={500}
          className="h-auto w-full"
        />
        {/* 左矢印 */}
        <button className="absolute top-0 bottom-0 left-5 my-auto max-md:top-auto max-md:-bottom-13 max-md:left-3">
          <Image src={leftArrow} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        </button>
        {/* 右矢印 */}
        <button className="absolute top-0 right-5 bottom-0 my-auto max-md:top-auto max-md:right-3 max-md:-bottom-13">
          <Image src={rightArrow} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        </button>
        {/* 拡大 */}
        <button className="absolute top-4 right-5 max-md:hidden">
          <Image src={zoom} alt="" width={30} height={30} />
        </button>
        {/* ダウンロード */}
        <button className="absolute right-5 bottom-4 max-md:right-3 max-md:bottom-3">
          <Image src={download} alt="" width={30} height={30} className="max-md:h-6 max-md:w-6" />
        </button>
      </div>
      {/* ナビゲーション サムネイル */}
      <div className="mx-auto mt-5 grid w-fit grid-cols-5 gap-2.5 max-md:mt-4 max-md:gap-2">
        <Image
          src="/images/mock-img.jpg"
          alt=""
          width={80}
          height={80}
          className="aspect-square h-20 w-20 object-cover opacity-40 max-md:h-12 max-md:w-12"
        />
        <Image
          src="/images/mock-img.jpg"
          alt=""
          width={80}
          height={80}
          className="aspect-square h-20 w-20 object-cover opacity-70 max-md:h-12 max-md:w-12"
        />
        <Image
          src="/images/mock-img.jpg"
          alt=""
          width={80}
          height={80}
          className="aspect-square h-20 w-20 object-cover max-md:h-12 max-md:w-12"
        />
        <Image
          src="/images/mock-img.jpg"
          alt=""
          width={80}
          height={80}
          className="aspect-square h-20 w-20 object-cover opacity-70 max-md:h-12 max-md:w-12"
        />
        <Image
          src="/images/mock-img.jpg"
          alt=""
          width={80}
          height={80}
          className="aspect-square h-20 w-20 object-cover opacity-40 max-md:h-12 max-md:w-12"
        />
      </div>
      <div className="max-md:mt-5 max-md:flex max-md:items-center max-md:justify-between max-md:px-5">
        {isModal ? (
          <button
            onClick={handleClose}
            className="border-line-gray mx-auto mt-10 grid h-10 w-35 place-content-center rounded-lg border bg-white max-md:m-0 max-md:h-9 max-md:w-30 max-md:text-xs"
          >
            一覧に戻る
          </button>
        ) : (
          <Link
            href="/media"
            className="border-line-gray mx-auto mt-10 grid h-10 w-35 place-content-center rounded-lg border bg-white max-md:m-0 max-md:h-9 max-md:w-30 max-md:text-xs"
          >
            一覧に戻る
          </Link>
        )}
        <div className="md:hidden">
          <FavoriteMediaDetail />
        </div>
      </div>
    </div>
  );
};
