"use client";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";

import { AlbumResponseDto } from "@/lib/api-client/gen";

type Props = {
  album: AlbumResponseDto;
};

/**
 * アルバム画像が複数ある時のみ呼び出されるスライドショーコンポーネント
 */
export const AlbumsGridSlide = ({ album }: Props) => {
  const [isHovered, setIsHovered] = useState(false);
  const [activeIndex, setActiveIndex] = useState(0);

  useEffect(() => {
    let intervalId: NodeJS.Timeout;

    // ホバー中のみスライド処理を実行
    if (isHovered) {
      intervalId = setInterval(() => {
        // 次の画像のindexを現在のインデックスから計算
        setActiveIndex((prev) => (prev + 1) % album.coverMediaUrls.length);
      }, 1000);
    }

    // アンマウント時やホバー状態の変化時にタイマーをクリーンアップ
    return () => {
      if (intervalId) clearInterval(intervalId);
    };
  }, [isHovered, album.coverMediaUrls.length]);

  // ホバーが外れた時のリセット処理
  const handleMouseLeave = () => {
    setIsHovered(false);
    setActiveIndex(0);
  };

  return (
    <Link
      href={`/albums/${album.id}`}
      className="group relative block aspect-square overflow-hidden"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={handleMouseLeave}
    >
      {album.coverMediaUrls.map((url, index) => (
        <Image
          src={url ?? "/images/no-thumbnail.png"}
          alt=""
          className={`absolute top-0 right-0 bottom-0 left-0 aspect-square transition-all duration-700 group-hover:scale-110 ${
            index === activeIndex ? "opacity-100" : "opacity-0"
          }`}
          width={245}
          height={245}
          key={url}
        />
      ))}
    </Link>
  );
};
