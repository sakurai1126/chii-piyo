import Image from "next/image";

export const ShareGroupMediaDetail = () => {
  return (
    <div className="mt-7">
      <p className="max-md:text-sm">共有範囲</p>
      <div className="mt-2.5 flex items-center justify-between max-md:flex-col max-md:items-start">
        <div className="flex items-center gap-2">
          <p className="text-sm max-md:text-xs">家族全員</p>
          <div className="bg-line-gray h-px w-7"></div>
          {[1, 2, 3, 4].map((item) => (
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
        <button className="text-sm underline max-md:mt-3 max-md:ml-auto">共有範囲を変更する</button>
      </div>
    </div>
  );
};
