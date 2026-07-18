"use client";
import { useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { useMemo, useTransition } from "react";

import { toast } from "@/components/ui/Toast";
import { MediaResponseDto, UserResponseDto } from "@/lib/api-client/gen";
import { cn } from "@/utils/cn";

import { addFavoriteAction } from "../actions/addFavoriteAction";
import { removeFavoriteAction } from "../actions/removeFavoriteAction";

type Props = {
  media: MediaResponseDto;
  users: UserResponseDto[];
};

export const FavoriteMediaDetail = ({ media, users }: Props) => {
  // 一覧画面のtanstack queryのキャッシュ破棄用フック
  const queryClient = useQueryClient();
  const [isPending, startTransition] = useTransition();

  const userMap = useMemo(() => {
    const map = new Map<number, UserResponseDto>();
    users.forEach((user) => map.set(user.id, user));
    return map;
  }, [users]);

  const favoriteToggle = () => {
    startTransition(async () => {
      const result = media.isFavorite
        ? await removeFavoriteAction({ mediaId: media.id })
        : await addFavoriteAction({ mediaId: media.id });

      if (result.success) {
        toast.success(media.isFavorite ? "お気に入りを解除しました" : "お気に入りに追加しました");

        // 一覧画面のキャッシュを破棄する
        queryClient.invalidateQueries({ queryKey: ["media"] });
      } else {
        toast.error(result.error);
      }
    });
  };

  return (
    <div className="flex items-start gap-2">
      <div className="flex -space-x-2">
        {media.addFavoriteUserIds?.map((id) => (
          <div
            key={id}
            className="h-7.5 w-7.5 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px"
          >
            <Image
              src={userMap.get(id)?.presignedIconUrl || "/images/no-image.svg"}
              alt=""
              width={29}
              height={29}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
        ))}
      </div>
      <button type="button" className="group" onClick={favoriteToggle} disabled={isPending}>
        <svg width="30" height="30" viewBox="0 0 25 25" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M16.9994 5.02466C16.4486 5.00987 15.9005 5.10755 15.3887 5.31173C14.8769 5.5159 14.4121 5.82229 14.0227 6.21216C13.6729 6.55831 13.2007 6.75247 12.7086 6.75247C12.2164 6.75247 11.7442 6.55831 11.3944 6.21216C11.007 5.818 10.5431 5.50722 10.0312 5.29893C9.51933 5.09063 8.97024 4.98921 8.41772 5.00091C5.30647 5.00091 3.72313 7.88257 4.0398 10.5426C4.31688 12.7988 6.01105 15.0313 7.6023 16.6226C8.94336 17.8613 10.4138 18.9521 11.9881 19.8763C12.2066 20.0044 12.4553 20.0718 12.7086 20.0718C12.9618 20.0718 13.2105 20.0044 13.429 19.8763C15.0033 18.9521 16.4737 17.8613 17.8148 16.6226C19.3981 15.0392 21.1002 12.7988 21.3773 10.5426C21.694 7.90632 20.1106 5.02466 16.9994 5.02466Z"
            strokeLinecap="round"
            strokeLinejoin="round"
            className={cn(
              "stroke-brown-dark transition-all",
              media.isFavorite
                ? "fill-accent-pink group-hover:fill-accent-pink/80"
                : "group-hover:fill-accent-pink/20 fill-white",
            )}
          />
        </svg>
      </button>
    </div>
  );
};
