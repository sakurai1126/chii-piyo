import Image from "next/image";
import Link from "next/link";

import { FavoriteMediaList } from "@/features/favorite/components/FavoriteMediaList";
import { MediaListResponseDto, UserResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import commentIcon from "../assets/comment.svg";
import videoIcon from "../assets/video-icon.svg";

type Props = {
  isEasy: boolean;
  favoriteData: MediaListResponseDto;
  mediaData: MediaListResponseDto;
  users: UserResponseDto[];
};

export const TopMedia = ({ isEasy, favoriteData, mediaData, users }: Props) => {
  // お気に入り画像が3枚未満の場合は通常画像をメイン表示にする
  let mainMedia = favoriteData.items.length >= 3 ? favoriteData.items : mediaData.items;

  // 画像が1枚以上かつ6枚未満の場合は、6枚になるまで元の画像を繰り返して補完
  if (mainMedia.length > 0 && mainMedia.length < 6) {
    mainMedia = Array.from({ length: 6 }, (_, i) => mainMedia[i % mainMedia.length]);
  }

  const today = new Date();
  const birthday = new Date("2025-08-06");

  // 生後日数の計算
  const diffDays = Math.floor((today.getTime() - birthday.getTime()) / 86400000);

  // 年の差分を月数に変換
  const diffYearsToMonth = (today.getFullYear() - birthday.getFullYear()) * 12;

  // 月齢の計算
  let months = diffYearsToMonth + today.getMonth() - birthday.getMonth();

  // 月誕生日を過ぎているかどうかで月数を調整
  if (today.getDate() < birthday.getDate()) months--;
  return (
    (mainMedia.length > 0 || mediaData.items.length > 0) && (
      <div className={cn("mx-auto mt-20 @max-md:mt-6", !isEasy && "max-w-310 px-5")}>
        <div className={cn("flex justify-between gap-5 @max-md:flex-col", isEasy && "gap-3")}>
          {/* お気に入り画像 */}
          <div
            className={cn(
              "relative aspect-square h-[570px] w-[570px] shrink-0 overflow-hidden",
              isEasy
                ? "h-full w-full"
                : "@max-lg:h-100 @max-lg:w-100 @max-md:h-full @max-md:w-full",
            )}
          >
            {mainMedia.map((media, index) => (
              <div
                key={`${index}-${media.id}`}
                className="animate-fade-slideshow absolute inset-0 bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
                style={{ animationDelay: `-${30 - index * 5}s` }}
              >
                <Image
                  src={media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
                  alt=""
                  className="bg-background h-full w-full object-cover"
                  width={570}
                  height={570}
                  priority={index === 0} // 1枚目のみ優先読み込み
                />
              </div>
            ))}
          </div>
          <div className="flex w-full flex-col justify-between">
            {!isEasy && (
              <div>
                <p>
                  <span className="text-[40px] font-medium @max-md:text-3xl">
                    {today.getFullYear()}
                  </span>
                  <span className="mr-3 ml-1 text-2xl @max-md:text-lg">年</span>
                  <span className="text-[40px] font-medium @max-md:text-3xl">
                    {today.getMonth() + 1}
                  </span>
                  <span className="mr-3 ml-1 text-2xl @max-md:text-lg">月</span>
                  <span className="text-[40px] font-medium @max-md:text-3xl">
                    {today.getDate()}
                  </span>
                  <span className="mr-3 ml-1 text-2xl @max-md:text-lg">日</span>
                </p>
                <p>
                  <span className="text-note-gray text-xl @max-md:text-[16px]">{`${Math.floor(months / 12)}歳${months % 12}ヵ月`}</span>
                  <span className="text-note-gray ml-3 text-sm">生後{diffDays}日</span>
                </p>
              </div>
            )}

            {/* 画像表示 */}
            <div
              className={cn(
                "grid w-full grid-cols-3 gap-5 @max-lg:gap-2",
                isEasy ? "grid-cols-2" : "@max-md:mt-3",
              )}
            >
              {mediaData.items.map((data) => (
                <Link
                  href={`/media/${data.id}`}
                  className="group bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
                  key={data.id}
                  scroll={false}
                >
                  <div className="relative overflow-hidden">
                    <Image
                      src={data.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
                      alt=""
                      className="bg-background h-full w-full object-cover transition-all duration-500 group-hover:scale-110 @max-lg:max-h-30 @max-md:max-h-full"
                      width={190}
                      height={190}
                    />

                    {data.mediaType === "VIDEO" && (
                      <Image
                        src={videoIcon}
                        alt=""
                        width={40}
                        height={40}
                        className="absolute top-0 right-0 bottom-0 left-0 m-auto"
                      />
                    )}

                    {!isEasy && (
                      <>
                        {/* お気に入り */}
                        <FavoriteMediaList media={data} users={users} />
                        {/* コメント */}
                        {data.commentCount ? (
                          <div className="border-brown-dark bg-accent-orange-back absolute right-2 bottom-2 flex items-center gap-1 rounded-2xl border px-2 py-0.5 @max-md:right-1 @max-md:bottom-1">
                            <Image
                              src={commentIcon}
                              alt="comment"
                              width={11}
                              height={11}
                              className="mt-0.5 @max-md:h-4 @max-md:w-4"
                            />
                            <p className="text-brown-dark text-xs @max-md:text-[10px]">
                              {data.commentCount}
                            </p>
                          </div>
                        ) : null}
                      </>
                    )}
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </div>
        <Link
          href="/media"
          className={cn(
            "border-brown-middle bg-brown-back text-brown-middle mt-10 ml-auto flex h-12 w-50 cursor-pointer items-center justify-center gap-3 rounded-lg border px-7 font-medium transition-all duration-300 @max-md:mx-auto @max-md:mt-6 @max-md:gap-2 @max-md:px-3",
            isEasy
              ? "border-brown-dark bg-brown-light text-[16px] text-white"
              : "hover:bg-brown-light text-sm hover:text-white @max-md:h-10 @max-md:text-xs",
          )}
        >
          写真・動画一覧
        </Link>
      </div>
    )
  );
};
