import Image from "next/image";

import comment from "../assets/comment.svg";
import heart from "../assets/heart.svg";

export const MediaListItem = () => {
  return (
    <div className="group relative aspect-square overflow-hidden">
      <Image
        src="/images/mock-img.jpg"
        alt=""
        width={235}
        height={235}
        className="absolute transition-all duration-500 group-hover:scale-125"
      />
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
      <div className="absolute right-2 bottom-2 flex items-center gap-1 max-md:right-1 max-md:bottom-1">
        <Image
          src={comment}
          alt="comment"
          width={20}
          height={20}
          className="max-md:h-4 max-md:w-4"
        />
        <p className="text-brown-dark max-md:text-xs">1</p>
      </div>
    </div>
  );
};
