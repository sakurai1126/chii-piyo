import { FavoriteMediaDetail } from "@/features/favorite";

export const MediaMetaData = () => {
  return (
    <div className="mt-6 flex justify-between">
      <div>
        <p className="text-[20px] max-md:text-lg">IMG_0001.jpg</p>

        <div className="mt-3 grid gap-2 text-[13px] max-md:text-xs">
          <p>サイズ : 5.2MB</p>
          <p>解像度 : 1000 × 1000</p>
          <p>日付 : 2026年1月1日</p>
        </div>
      </div>
      <div className="max-md:hidden">
        <FavoriteMediaDetail />
      </div>
    </div>
  );
};
