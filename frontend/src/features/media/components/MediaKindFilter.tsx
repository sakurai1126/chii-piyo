import Image from "next/image";

import icon from "../assets/file-type-icon.svg";

type Props = {
  updateFilter: ({ key, value }: { key: string; value: string }) => void;
  currentValue?: string;
};

export const MediaKindFilter = ({ updateFilter, currentValue = "" }: Props) => {
  return (
    <div className="bg-brown-back shrink-0 rounded-lg px-7 pt-6 pb-8 max-md:p-3 max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="max-md:text-[13px]">写真/動画</p>
      </div>
      <div className="mt-3 flex gap-2">
        <label
          htmlFor="allMedia"
          className="has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle border-line-gray flex cursor-pointer items-center gap-2 rounded-lg border bg-white py-1.5 pr-5 pl-3 transition-all max-md:py-1"
        >
          <input
            type="radio"
            id="allMedia"
            name="mediaType"
            checked={currentValue === ""}
            onChange={() => {
              updateFilter({ key: "mediaKind", value: "" });
            }}
            className="accent-accent-orange-radio"
          />
          <p className="text-sm max-md:text-xs">すべて</p>
        </label>
        <label
          htmlFor="photo"
          className="has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle border-line-gray flex cursor-pointer items-center gap-2 rounded-lg border bg-white py-1.5 pr-5 pl-3 transition-all max-md:py-1"
        >
          <input
            type="radio"
            id="photo"
            name="mediaType"
            checked={currentValue === "PHOTO"}
            onChange={(e) => {
              updateFilter({ key: "mediaKind", value: e.target.checked ? "PHOTO" : "" });
            }}
            className="accent-accent-orange-radio"
          />
          <p className="text-sm max-md:text-xs">写真</p>
        </label>
        <label
          htmlFor="video"
          className="has-checked:border-accent-orange has-checked:bg-accent-orange-back has-checked:text-brown-middle border-line-gray flex cursor-pointer items-center gap-2 rounded-lg border bg-white py-1.5 pr-5 pl-3 transition-all max-md:py-1"
        >
          <input
            type="radio"
            id="video"
            name="mediaType"
            checked={currentValue === "VIDEO"}
            onChange={(e) => {
              updateFilter({ key: "mediaKind", value: e.target.checked ? "VIDEO" : "" });
            }}
            className="accent-accent-orange-radio"
          />
          <p className="text-sm max-md:text-xs">動画</p>
        </label>
      </div>
    </div>
  );
};
