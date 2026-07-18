"use client";

import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";

import { AccentButton } from "@/components/ui/AccentButton";
import { Button } from "@/components/ui/Button";
import { FavoriteMediaDetail } from "@/features/favorite";
import {
  MediaNavigationResponseDto,
  MediaResponseDto,
  UserResponseDto,
} from "@/lib/api-client/gen";

import download from "../../assets/download.svg";
import leftArrow from "../../assets/left-arrow.svg";
import rightArrow from "../../assets/right-arrow.svg";
import shrink from "../../assets/shrink.svg";
import zoom from "../../assets/zoom.svg";

type Props = {
  isEasy: boolean;
  media: MediaResponseDto;
  isModal?: boolean;
  users: UserResponseDto[];
};

export const MediaViewer = ({ isEasy, media, isModal, users }: Props) => {
  const router = useRouter();
  const [modeExpansion, setModeExpansion] = useState(false);
  return (
    <div className="w-125 shrink-0 @max-xl:w-110 @max-md:w-full">
      {/* 拡大モード */}
      {modeExpansion && (
        <div className="fixed top-0 left-0 z-1000 grid h-full w-full place-content-center bg-black">
          <button
            type="button"
            className="fixed top-5 right-5 cursor-pointer transition-all hover:opacity-50 @max-md:hidden"
            onClick={() => setModeExpansion(false)}
          >
            <Image src={shrink} alt="" width={25} height={25} />
          </button>
          <Image
            src={media.presignedUrl || "/images/media-error.png"}
            alt={media.originalFilename ?? ""}
            width={media.width ?? 500}
            height={media.height ?? 500}
            className="h-screen w-full object-contain"
            priority
          />
        </div>
      )}
      <div className="relative">
        {/* 画像、動画表示 */}
        {media.mediaType == "PHOTO" && (
          <Image
            src={media.presignedUrl || "/images/media-error.png"}
            alt={media.originalFilename ?? ""}
            width={500}
            height={media.height && media.width ? (media.height / media.width) * 500 : 500}
            className="h-auto w-full"
            priority
          />
        )}
        {media.mediaType == "VIDEO" && (
          <video src={media.presignedUrl} controls className="h-auto w-full" /> // NOSONAR: 字幕の必要なユーザーを想定していないため
        )}
        {/* 左矢印 */}
        {media.nextMedia &&
          !isEasy &&
          (isModal ? (
            <Link
              href={`/media/${media.nextMedia.id}`}
              className="border-brown-middle bg-brown-back absolute top-0 bottom-0 left-5 my-auto block h-fit w-fit cursor-pointer rounded-full border p-1 transition-all hover:opacity-50 @max-md:top-auto @max-md:-bottom-13 @max-md:left-3 @max-md:hidden"
              replace={true}
              scroll={false}
            >
              <Image
                src={leftArrow}
                alt=""
                width={20}
                height={20}
                className="@max-md:h-6 @max-md:w-6"
              />
            </Link>
          ) : (
            // モーダル表示状態じゃない場合現在の詳細の上にモーダル表示されるためフルロードで遷移
            <a
              href={`/media/${media.nextMedia.id}`}
              className="border-brown-middle bg-brown-back absolute top-0 bottom-0 left-5 my-auto block h-fit w-fit cursor-pointer rounded-full border p-1 transition-all hover:opacity-50 @max-md:top-auto @max-md:-bottom-13 @max-md:left-3 @max-md:hidden"
            >
              <Image
                src={leftArrow}
                alt=""
                width={20}
                height={20}
                className="@max-md:h-6 @max-md:w-6"
              />
            </a>
          ))}

        {/* 右矢印 */}
        {media.previousMedia &&
          !isEasy &&
          (isModal ? (
            <Link
              href={`/media/${media.previousMedia.id}`}
              className="border-brown-middle bg-brown-back absolute top-0 right-5 bottom-0 my-auto block h-fit w-fit cursor-pointer rounded-full border p-1 transition-all hover:opacity-50 @max-md:top-auto @max-md:-bottom-13 @max-md:left-3 @max-md:hidden"
              replace={true}
              scroll={false}
            >
              <Image
                src={rightArrow}
                alt=""
                width={20}
                height={20}
                className="@max-md:h-6 @max-md:w-6"
              />
            </Link>
          ) : (
            // モーダル表示状態じゃない場合現在の詳細の上にモーダル表示されるためフルロードで遷移
            <a
              href={`/media/${media.previousMedia.id}`}
              className="border-brown-middle bg-brown-back absolute top-0 right-5 bottom-0 my-auto block h-fit w-fit cursor-pointer rounded-full border p-1 transition-all hover:opacity-50 @max-md:top-auto @max-md:-bottom-13 @max-md:left-3 @max-md:hidden"
            >
              <Image
                src={rightArrow}
                alt=""
                width={20}
                height={20}
                className="@max-md:h-6 @max-md:w-6"
              />
            </a>
          ))}

        {media.mediaType == "PHOTO" && (
          <>
            {/* 拡大 */}
            <button
              type="button"
              className="absolute top-4 right-5 cursor-pointer transition-all hover:opacity-70 @max-md:hidden"
              onClick={() => setModeExpansion(true)}
            >
              <Image src={zoom} alt="" width={30} height={30} />
            </button>
            {/* ダウンロード */}
            <a
              href={media.presignedUrl ?? "#"}
              download
              className="absolute right-5 bottom-4 cursor-pointer transition-all hover:opacity-70 @max-md:right-3 @max-md:bottom-3"
            >
              <Image
                src={download}
                alt=""
                width={30}
                height={30}
                className="@max-md:h-6 @max-md:w-6"
              />
            </a>
          </>
        )}
      </div>
      {/* ナビゲーション サムネイル */}
      {!isEasy && (
        <div className="mx-auto mt-5 grid w-fit grid-cols-5 gap-2.5 @max-md:mt-4 @max-md:gap-2">
          {/* 2つ先のメディア */}
          {media.secondNextMedia ? (
            <NavigationThumbnail media={media.secondNextMedia} isModal={isModal} />
          ) : (
            <div></div>
          )}
          {/* 次のメディア */}
          {media.nextMedia ? (
            <NavigationThumbnail media={media.nextMedia} isModal={isModal} />
          ) : (
            <div></div>
          )}
          {/* 現在のメディア */}
          <Image
            src={media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
            alt="現在のメディア"
            width={70}
            height={70}
            className="aspect-square h-17.5 w-17.5 object-cover opacity-100 @max-md:h-12 @max-md:w-12"
          />
          {/* 前のメディア */}
          {media.previousMedia ? (
            <NavigationThumbnail media={media.previousMedia} isModal={isModal} />
          ) : (
            <div></div>
          )}
          {/* 2つ前のメディア */}
          {media.secondPreviousMedia ? (
            <NavigationThumbnail media={media.secondPreviousMedia} isModal={isModal} />
          ) : (
            <div></div>
          )}
        </div>
      )}
      <div className="@max-md:mt-5 @max-md:flex @max-md:items-center @max-md:justify-between @max-md:px-5">
        {isModal ? (
          <Button
            variant="cancel"
            onClick={() => router.back()}
            className="mx-auto mt-10 block @max-md:m-0"
          >
            戻る
          </Button>
        ) : (
          <AccentButton
            variant="link"
            styleVariant="cancel"
            href="/media"
            className="mt-10 @max-md:m-0"
          >
            メディア一覧
          </AccentButton>
        )}
        <div className="@md:hidden">
          <FavoriteMediaDetail media={media} users={users} />
        </div>
      </div>
    </div>
  );
};

const NavigationThumbnail = ({
  media,
  isModal,
}: {
  media: MediaNavigationResponseDto;
  isModal?: boolean;
}) => {
  return isModal ? (
    <Link href={`/media/${media.id}`} replace={true} scroll={false}>
      <Image
        src={media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
        alt=""
        width={70}
        height={70}
        className="aspect-square h-17.5 w-17.5 object-cover opacity-70 @max-md:h-12 @max-md:w-12"
      />
    </Link>
  ) : (
    <a href={`/media/${media.id}`}>
      <Image
        src={media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
        alt=""
        width={70}
        height={70}
        className="aspect-square h-17.5 w-17.5 object-cover opacity-70 @max-md:h-12 @max-md:w-12"
      />
    </a>
  );
};
