import Image from "next/image";

import heart from "../assets/heart.svg";

export const FavoriteMediaDetail = () => {
  return (
    <div className="flex items-start gap-2">
      <div className="flex -space-x-2">
        {[1, 2, 3].map((item) => (
          <div
            key={item}
            className="h-7.5 w-7.5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
          >
            <Image
              src="/images/mock-img.jpg"
              alt=""
              width={29}
              height={29}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
        ))}
      </div>
      <Image src={heart} alt="" width={30} height={30} />
    </div>
  );
};
