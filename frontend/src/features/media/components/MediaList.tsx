import { MediaListResponseDto } from "../../../lib/api-client/gen/models/MediaListResponseDto";

import { MediaListItem } from "./MediaListItem";

interface MediaListProps {
  data: MediaListResponseDto;
}

export const MediaList = ({ data }: MediaListProps) => {
  return (
    <>
      <div className="mt-10 flex items-center gap-10">
        <p className="text-note-gray shrink-0 text-2xl font-light max-md:text-sm">2026年2月</p>
        <div className="bg-line-gray h-px w-full max-md:hidden"></div>
      </div>

      <div className="mt-4 ml-7 grid grid-cols-4 gap-2 max-md:mt-2 max-md:ml-0 max-md:grid-cols-3 max-md:gap-0.5">
        {data.items.map((item) => (
          <MediaListItem key={item.id} data={item} />
        ))}
      </div>

      <div className="mt-10 flex items-center gap-10 max-md:mt-5">
        <p className="text-note-gray shrink-0 text-2xl font-light max-md:text-sm">2026年1月</p>
        <div className="bg-line-gray h-px w-full max-md:hidden"></div>
      </div>

      <div className="mt-4 ml-7 grid grid-cols-4 gap-2 max-md:mt-2 max-md:ml-0 max-md:grid-cols-3 max-md:gap-0.5"></div>
    </>
  );
};
