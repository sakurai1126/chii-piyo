import Image from "next/image";

import { Button } from "@/components/ui/Button";

export const AlbumMediaDetail = () => {
  return (
    <div className="mt-7">
      <p className="max-md:text-sm">アルバム</p>
      <div className="border-brown-dark mt-2 flex justify-between rounded-lg border bg-[rgba(255,255,255,0.5)] p-4 backdrop-blur-[7.5px] max-md:flex-col">
        <div className="flex gap-3">
          <Image
            src="/images/mock-img.jpg"
            alt=""
            width={80}
            height={80}
            className="aspect-square h-20 w-20 rounded-sm object-cover"
          />
          <div>
            <p className="text-sm max-md:text-[13px]">タイトル</p>
            <p className="mt-1 text-xs max-md:text-[11px]">画像：1枚</p>
            <p className="mt-1 text-xs max-md:text-[11px]">動画：1本</p>
          </div>
        </div>
        <div className="flex flex-col items-end justify-between max-md:mt-3 max-md:flex-row max-md:items-center">
          <Button className="w-32 max-md:h-8 max-md:w-30">アルバムを見る</Button>
          <button className="text-warning text-xs underline max-md:text-[10px]">
            アルバムから削除する
          </button>
        </div>
      </div>
    </div>
  );
};
