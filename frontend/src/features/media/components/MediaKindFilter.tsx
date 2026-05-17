"use client";

import Image from "next/image";
import { useState } from "react";

import checked from "../assets/checked.svg";
import icon from "../assets/file-type-icon.svg";
import plus from "../assets/plus.svg";

const labelClasses = {
  unchecked: "border-line-gray bg-white",
  checked: "border-accent-orange bg-accent-orange-back text-brown-middle",
};

export const MediaKindFilter = () => {
  const [isImageChecked, setIsImageChecked] = useState(false);
  const [isVideoChecked, setIsVideoChecked] = useState(false);

  const fileTypes = [
    { type: "image", name: "写真", isChecked: isImageChecked, setIsChecked: setIsImageChecked },
    { type: "video", name: "動画", isChecked: isVideoChecked, setIsChecked: setIsVideoChecked },
  ];

  return (
    <div className="bg-brown-back shrink-0 rounded-lg px-7 pt-6 pb-8 max-md:p-3 max-md:pb-4">
      <div className="flex items-center gap-1.5">
        <Image src={icon} alt="" width={32} height={32} className="h-6.5 w-6.5" />
        <p className="max-md:text-[13px]">写真/動画</p>
      </div>
      <div className="mt-3 flex gap-2">
        {fileTypes.map((fileType) => (
          <label
            key={fileType.type}
            htmlFor={fileType.type}
            className={`flex cursor-pointer items-center gap-2 rounded-4xl border py-1.5 pr-5 pl-3 transition-all max-md:py-1 ${fileType.isChecked ? labelClasses.checked : labelClasses.unchecked}`}
          >
            <input
              type="checkbox"
              id={fileType.type}
              checked={fileType.isChecked}
              onChange={(e) => fileType.setIsChecked(e.target.checked)}
              className="hidden"
            />
            <Image src={fileType.isChecked ? checked : plus} alt="" width={14} height={14} />
            <p className="text-sm max-md:text-xs">{fileType.name}</p>
          </label>
        ))}
      </div>
    </div>
  );
};
