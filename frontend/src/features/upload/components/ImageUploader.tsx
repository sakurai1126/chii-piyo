import Image from "next/image";
import { useRef } from "react";

import icon from "../assets/image-icon.svg";
import { useDragAndDrop } from "../hooks/useDragAndDrop";

type Props = {
  onFilesAdd: (files: File[]) => void;
};

export const ImageUploader = ({ onFilesAdd }: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { isDragging, handleDrop, handleDragEnter, handleDragLeave } = useDragAndDrop({
    onFilesAdd,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // ファイルが選択されたときにonFilesAddを呼び出しHooks内のuseStateにファイルとURLをセットする
    if (e.target.files) onFilesAdd([...e.target.files]);
    // 同じファイルを連続で選択したときもonFilesAddが呼ばれるようにinputの値をリセットする
    e.target.value = "";
  };

  return (
    <section
      aria-label="画像のドラッグ&ドロップエリア"
      className={`bg-brown-back border-brown-middle relative block rounded-4xl border-2 border-dotted px-5 pt-10 pb-15 text-center transition-all duration-500 max-md:rounded-2xl max-md:pb-5 ${isDragging ? "bg-white-back" : ""} `}
      onDrop={handleDrop}
      onDragOver={(e) => e.preventDefault()}
      onDragEnter={handleDragEnter}
      onDragLeave={handleDragLeave}
    >
      {/* 実態のinput要素を非表示にする */}
      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        multiple
        className="hidden"
        onChange={handleChange}
      />

      <p className="text-accent-pink bg-accent-pink-back absolute top-5 right-8 grid h-8 w-25 place-content-center rounded-2xl border text-xs font-medium max-md:top-2 max-md:right-2 max-md:h-5 max-md:w-18 max-md:text-[10px]">
        複数選択可
      </p>
      <Image
        src={icon}
        alt=""
        width={46}
        height={46}
        className="mx-auto max-md:h-8.5 max-md:w-8.5"
      />
      <p className="mt-5 text-xl font-medium max-md:mt-2.5 max-md:text-[13px]">
        写真をアップロード
      </p>
      <p className="text-note-gray mt-4 text-sm max-md:hidden">
        複数枚をまとめて選択できます
        <br />
        ドラッグ＆ドロップにも対応
      </p>
      <button
        className="bg-brown-middle border-brown-middle hover:text-brown-middle hover:bg-white-back mt-5 h-10 w-40 cursor-pointer rounded-4xl border font-medium text-white transition-all duration-500 max-md:hidden"
        onClick={() => inputRef.current?.click()}
      >
        写真を選択
      </button>
      <p className="text-note-gray mt-4 text-xs max-md:hidden">
        jpg / jpeg / png / heic - 1枚最大50MB
      </p>
      <p className="text-note-gray mt-2 text-[10px] leading-5 md:hidden">
        まとめて選択できます
        <br />
        1枚最大50MB
      </p>
    </section>
  );
};
