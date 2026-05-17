import Image from "next/image";

import { MediaResponseDto } from "@/lib/api-client/gen";

import comment from "../assets/comment.svg";
import heart from "../assets/heart.svg";

type Props = {
  data: MediaResponseDto;
};

export const MediaListItem = ({ data }: Props) => {
  return (
    <div className="group relative aspect-square overflow-hidden">
      {data.thumbnailPresignedUrl ? (
        <Image
          src={data.thumbnailPresignedUrl}
          alt=""
          fill
          className="absolute h-full w-full object-cover transition-all duration-500 group-hover:scale-110"
        />
      ) : (
        <Image
          src="/images/no-thumbnail.png"
          alt=""
          fill
          className="absolute h-full w-full object-cover transition-all duration-500 group-hover:scale-110"
        />
      )}

      {/* チェックボックス */}
      <input
        aria-label="選択"
        type="checkbox"
        className="accent-accent-pink absolute top-2 left-2 z-1 h-5 w-5 max-md:top-1 max-md:left-1 max-md:h-4 max-md:w-4"
      />
      {/* お気に入り */}
      <div className="absolute top-2 right-2 flex items-center gap-0.5 max-md:top-1 max-md:right-1">
        <div className="flex -space-x-2">
          <div className="h-5 w-5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px max-md:h-4 max-md:w-4">
            <Image
              src="/images/mock-img.jpg"
              alt=""
              width={19}
              height={19}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
          <div className="h-5 w-5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px max-md:h-4 max-md:w-4">
            <Image
              src="/images/mock-img.jpg"
              alt=""
              width={19}
              height={19}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
        </div>
        <Image src={heart} alt="" width={20} height={20} className="max-md:h-4 max-md:w-4" />
      </div>

      {/* コメント */}
      {data.commentCount ? (
        <div className="border-brown-dark bg-accent-orange-back absolute right-2 bottom-2 flex items-center gap-1 rounded-2xl border px-2 py-0.5 max-md:right-1 max-md:bottom-1">
          <Image
            src={comment}
            alt="comment"
            width={11}
            height={11}
            className="mt-0.5 max-md:h-4 max-md:w-4"
          />
          <p className="text-brown-dark text-xs max-md:text-[10px]">{data.commentCount}</p>
        </div>
      ) : null}
    </div>
  );
};
