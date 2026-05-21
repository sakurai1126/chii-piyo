import Image from "next/image";

import plus from "../assets/plus.svg";
export const TagMediaDetail = () => {
  return (
    <div className="mt-7 max-md:mt-4">
      <p className="max-md:text-sm">タグ</p>
      <div className="mt-3 flex flex-wrap gap-3">
        {[1, 2, 3].map((item) => (
          <p
            key={item}
            className="bg-accent-orange-back border-brown-middle text-brown-middle grid place-content-center rounded-2xl border px-4 py-1 text-sm max-md:px-3 max-md:text-xs"
          >
            タグ{item}
          </p>
        ))}
        <button className="border-line-gray text-note-gray flex items-center gap-1 rounded-2xl border border-dashed bg-[rgba(255,255,255,0.7)] px-3 py-1 text-sm max-md:text-xs">
          <Image src={plus} alt="" width={14} height={14} className="max-md:h-3 max-md:w-3" />
          <p>編集</p>
        </button>
      </div>
    </div>
  );
};
