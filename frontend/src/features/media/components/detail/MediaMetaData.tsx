import { FavoriteMediaDetail } from "@/features/favorite";
import { MediaResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateNonTime } from "@/utils/date";

type Props = {
  media: MediaResponseDto;
};

export const MediaMetaData = ({ media }: Props) => {
  const sizeInKB = (media.fileSize / 1024).toFixed(0);
  const sizeInMB = (media.fileSize / 1024 / 1024).toFixed(1);
  return (
    <div className="mt-6 flex justify-between">
      <div>
        <p className="text-[20px] max-md:text-lg">{media.originalFilename}</p>

        <div className="mt-3 grid gap-2 text-[13px] max-md:text-xs">
          <p>サイズ : {Number(sizeInKB) >= 1024 ? `${sizeInMB}MB` : `${sizeInKB}KB`}</p>
          {media.width && media.height && (
            <p>
              解像度 : {media.width} × {media.height}
            </p>
          )}
          {media.takenAt && <p>日付 : {formatJapaneseDateNonTime(media.takenAt)}</p>}
        </div>
      </div>
      <div className="max-md:hidden">
        <FavoriteMediaDetail />
      </div>
    </div>
  );
};
