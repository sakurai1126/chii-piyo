"use client";

import Image from "next/image";
import { useRef } from "react";

import { cn } from "@/utils/cn";

import icon from "../assets/image-icon.svg";
import { useDragAndDrop } from "../hooks/useDragAndDrop";

type Props = {
  isEasy: boolean;
  onFilesAdd: (files: File[]) => void;
  maxFiles: number;
  maxSize: number;
};

export const ImageUploader = ({ isEasy, onFilesAdd, maxFiles, maxSize }: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { isDragging, handleDrop, handleDragEnter, handleDragLeave } = useDragAndDrop({
    onFilesAdd,
    acceptTypes: "image",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // ファイルが選択されたときにonFilesAddを呼び出しHooks内のuseStateにファイルとURLをセットする
    if (e.target.files) {
      // 画像ファイルのみをフィルタリングしてonFilesAddを呼び出す
      const files = [...e.target.files].filter((f) => f.type.startsWith("image/"));
      if (files.length) onFilesAdd(files);
    }
    // 同じファイルを連続で選択したときもonFilesAddが呼ばれるようにinputの値をリセットする
    e.target.value = "";
  };

  return (
    <section
      aria-label="画像のドラッグ&ドロップエリア"
      className={cn(
        "border-brown-middle dark:border-note-gray bg-brown-back dark:bg-dark-image-back relative block rounded-4xl border-2 border-dotted pt-10 pb-15 text-center transition-all duration-500 @max-md:rounded-2xl @max-md:pb-5",
        isDragging && "bg-background-normal",
        isEasy && "pt-5",
      )}
      onDrop={handleDrop}
      onDragOver={(e) => e.preventDefault()}
      onDragEnter={handleDragEnter}
      onDragLeave={handleDragLeave}
    >
      {/* スマホタップ用ボタン */}
      <button
        className="absolute top-0 left-0 z-1 hidden h-full w-full rounded-2xl opacity-0 @max-md:block"
        onClick={() => inputRef.current?.click()}
      />
      {/* 実態のinput要素を非表示にする */}
      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        multiple
        className="hidden"
        onChange={handleChange}
      />
      {!isEasy && (
        <p className="text-accent-pink bg-accent-pink-back absolute top-5 right-8 grid h-8 w-30 place-content-center rounded-2xl border text-xs font-medium @max-md:top-2 @max-md:right-2 @max-md:h-5 @max-md:w-22 @max-md:text-[10px]">
          最大{maxFiles}ファイル
        </p>
      )}

      <Image
        src={icon}
        alt=""
        width={46}
        height={46}
        className={cn("mx-auto @max-md:h-8.5 @max-md:w-8.5", isEasy && "@max-md:h-10 @max-md:w-10")}
      />
      <p
        className={cn(
          "mt-5 text-xl font-medium @max-md:mt-2.5 @max-md:text-[13px]",
          isEasy && "@max-md:text-[16px]",
        )}
      >
        写真をアップロード
      </p>
      <p className="text-note-gray mt-4 text-sm @max-md:hidden">
        複数ファイルをまとめて選択できます
        <br />
        ドラッグ＆ドロップにも対応
      </p>
      <button
        className="bg-brown-middle dark:bg-brown-light border-brown-middle hover:text-brown-middle hover:bg-white-back mt-5 h-10 w-40 cursor-pointer rounded-4xl border font-medium text-white transition-all duration-500 @max-md:hidden"
        onClick={() => inputRef.current?.click()}
      >
        写真を選択
      </button>
      <p className="text-note-gray mt-4 text-xs @max-md:hidden">
        jpg / jpeg / png / heic - 1枚最大{maxSize}MB
      </p>
      <p
        className={cn(
          "text-note-gray mt-2 text-[10px] leading-5 @md:hidden",
          isEasy && "text-[13px]",
        )}
      >
        {"まとめて選択できます"}
        <span hidden={isEasy}>
          <br />
          1枚最大{maxSize}MB
        </span>
      </p>
    </section>
  );
};
