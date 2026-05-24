"use client";
import Image from "next/image";
import { useState } from "react";

import { Button } from "@/components/ui/Button";
import { type MediaCommentResponseDto, UserResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDate } from "@/utils/date";

type Props = {
  comments: MediaCommentResponseDto[];
  currentUser: UserResponseDto;
};

export const MediaComment = ({ comments, currentUser }: Props) => {
  const [isCommentMode, setIsCommentMode] = useState(false);
  return (
    <>
      <p className="max-md:text-sm">コメント</p>
      {comments.length !== 0 || isCommentMode ? (
        <div className="border-brown-dark mt-2 rounded-lg border bg-[rgba(255,255,255,0.5)] px-4 py-6 backdrop-blur-[7.5px]">
          <div className="grid gap-6">
            {comments.map((comment) => (
              <div className="flex items-start justify-between max-md:flex-col" key={comment.id}>
                <div className="flex items-start gap-4">
                  <div className="h-8 w-8 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
                    <Image
                      src={"/images/mock-img.jpg"}
                      alt=""
                      width={31}
                      height={31}
                      className="h-full w-full rounded-full object-cover"
                    />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="text-sm max-md:text-[13px]">{comment.displayName}</p>
                      <p className="text-xs text-gray-500 max-md:text-[11px]">
                        {formatJapaneseDate(comment.createdAt)}
                      </p>
                    </div>
                    <p className="mt-2 text-sm max-md:mt-1 max-md:text-xs">{comment.content}</p>
                  </div>
                </div>
                {comment.userId === currentUser.id && (
                  <button className="text-warning text-xs underline max-md:mt-2 max-md:ml-auto max-md:text-[10px]">
                    コメントを削除する
                  </button>
                )}
              </div>
            ))}
          </div>
          {/* 新規コメント */}
          <div className={`flex items-start gap-4 ${isCommentMode ? "" : "mt-10"}`}>
            <div className="h-8 w-8 shrink-0 rounded-full bg-[linear-gradient(100deg,#FFC0AB_35%,#FFF829_65%)] p-px">
              <Image
                src="/images/mock-img.jpg"
                alt=""
                width={31}
                height={31}
                className="h-full w-full rounded-full object-cover"
              />
            </div>
            <textarea
              placeholder="コメントを入力してください"
              className="border-line-gray focus:outline-brown-light min-h-20 w-full rounded-sm border bg-white p-2 text-sm max-md:text-xs"
            ></textarea>
          </div>
          <Button className="mt-4 ml-auto block max-md:h-9 max-md:w-28 max-md:text-xs">
            コメントを追加する
          </Button>
        </div>
      ) : (
        <Button
          className="mt-4 block max-md:h-9 max-md:w-28 max-md:text-xs"
          onClick={() => setIsCommentMode(true)}
        >
          コメントする
        </Button>
      )}
    </>
  );
};
