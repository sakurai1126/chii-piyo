import Image from "next/image";
import Link from "next/link";
import { Dispatch, SetStateAction, useId } from "react";

import { FavoriteMediaList } from "@/features/favorite/components/FavoriteMediaList";
import { MediaResponseDto, UserResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import comment from "../../assets/comment.svg";
import videoIcon from "../../assets/video-icon.svg";

type Props = {
  isEasy: boolean;
  data: MediaResponseDto;
  isSelectionMode?: boolean;
  users: UserResponseDto[];
  selectedMedia?: number[];
  setSelectedMedia?: Dispatch<SetStateAction<number[]>>;
};

export const MediaListItem = ({
  isEasy,
  data,
  isSelectionMode,
  users,
  selectedMedia,
  setSelectedMedia,
}: Props) => {
  const uid = useId();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      // 重複しない場合のみ追加する
      setSelectedMedia?.((prev) => (prev.includes(data.id) ? prev : [...prev, data.id]));
    } else {
      setSelectedMedia?.((prev) => prev.filter((item) => item !== data.id));
    }
  };

  const inner = (
    <div className="group relative aspect-square overflow-hidden">
      <Image
        src={data.thumbnailPresignedUrl ? data.thumbnailPresignedUrl : "/images/no-thumbnail.png"}
        alt=""
        width={230}
        height={230}
        className={cn(
          "absolute h-full w-full object-cover transition-all duration-500",
          !isSelectionMode && "group-hover:scale-110",
        )}
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

      {/* チェックボックス */}
      {isSelectionMode && (
        <input
          id={uid}
          aria-label="選択"
          type="checkbox"
          className="accent-accent-pink absolute top-2 left-2 z-1 h-5 w-5 @max-md:top-1 @max-md:left-1 @max-md:h-4 @max-md:w-4"
          checked={selectedMedia?.includes(data.id)}
          onChange={handleChange}
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
                src={comment}
                alt="comment"
                width={11}
                height={11}
                className="mt-0.5 @max-md:h-4 @max-md:w-4"
              />
              <p className="text-brown-dark text-xs @max-md:text-[10px]">{data.commentCount}</p>
            </div>
          ) : null}
        </>
      )}
    </div>
  );

  return isSelectionMode ? (
    <label htmlFor={uid} className="cursor-pointer">
      {inner}
    </label>
  ) : (
    <Link href={`/media/${data.id}`} className="block" scroll={false}>
      {inner}
    </Link>
  );
};
