import Image from "next/image";

import arrow from "../assets/arrow.svg";

export const AlbumSelector = () => {
  return (
    <div>
      <p className="max-md:text-[13px]">アルバム</p>
      <div className="border-line-gray relative mt-2 h-12 w-115 max-w-full rounded-sm border bg-white max-md:h-9">
        <select
          name=""
          className="focus:outline-brown-light h-full w-full appearance-none px-4 max-md:px-3 max-md:text-[13px]"
        >
          <option value="">選択してください</option>
          <option value="">ダミー</option>
          <option value="">ダミー</option>
          <option value="">ダミー</option>
        </select>
        <Image
          src={arrow}
          alt=""
          width={13}
          height={7}
          className="pointer-events-none absolute top-0 right-5 bottom-0 my-auto"
        />
      </div>
    </div>
  );
};
