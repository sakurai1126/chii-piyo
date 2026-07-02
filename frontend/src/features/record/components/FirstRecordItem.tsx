import Image from "next/image";
import Link from "next/link";

import { FirstRecordResponseDto } from "@/lib/api-client/gen";
import { calculateDaysSinceBirth, formatJapaneseDateNonTime } from "@/utils/date";

type Props = {
  item: FirstRecordResponseDto;
  index: number;
};
export const FirstRecordItem = ({ item, index }: Props) => {
  const birthday = new Date("2025-08-06");

  return (
    <div className="relative flex items-center gap-10 py-2.5 max-md:gap-6 max-md:py-2">
      <div className="bg-brown-dark h-2 w-2 shrink-0 rounded-full"></div>
      <div
        className={`bg-brown-dark absolute left-1 h-full w-px ${index === 0 ? "top-[50%]" : ""}`}
      ></div>
      <div className="bg-white-back border-brown-dark w-full rounded-lg border px-6 pt-6 pb-4 max-md:p-3">
        <div className="flex items-center max-md:flex-col max-md:items-start">
          <p className="text-2xl font-medium max-md:text-lg">{item.title}</p>
          <span className="bg-line-gray mr-3 ml-6 h-px w-6 max-md:hidden"></span>
          <p className="text-note-gray text-sm max-md:mt-1">
            {formatJapaneseDateNonTime(item.achievedDate)}&emsp;
            {calculateDaysSinceBirth(birthday, item.achievedDate)}目
          </p>
        </div>
        <p className="mt-5 max-md:mt-3 max-md:text-sm">{item.comment}</p>
        <div className="mt-5 flex flex-wrap gap-3 max-md:gap-2">
          {item.media.map((media) => (
            <Link
              href={`/media/${media.id}`}
              className="transition-all hover:opacity-70"
              key={media.id}
              scroll={false}
            >
              <Image
                src={media.thumbnailPresignedUrl ?? "/images/no-thumbnail.png"}
                alt=""
                className="rounded-sm max-md:h-12 max-md:w-12"
                width={80}
                height={80}
              />
            </Link>
          ))}
        </div>
        <div className="mt-3 ml-auto flex w-fit gap-3">
          <button className="cursor-pointer underline transition-all hover:opacity-70 max-md:text-xs">
            編集
          </button>
          <button className="text-warning cursor-pointer underline transition-all hover:opacity-70 max-md:text-xs">
            削除
          </button>
        </div>
      </div>
    </div>
  );
};
